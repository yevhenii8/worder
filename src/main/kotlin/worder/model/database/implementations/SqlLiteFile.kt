package worder.model.database.implementations

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Concat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Random
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder.case
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.Sum
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.intParam
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteConfig.TransactionMode.IMMEDIATE
import worder.BaseDatabaseWord
import worder.DatabaseWord
import worder.UpdatedWord
import worder.Word
import worder.model.database.WorderDB
import worder.model.database.WorderInsertDB
import worder.model.database.WorderUpdateDB
import worder.model.database.WorderUpdateDB.SelectOrder
import worder.model.database.WorderUpdateDB.SelectOrder.ASC
import worder.model.database.WorderUpdateDB.SelectOrder.DESC
import worder.model.database.WorderUpdateDB.SelectOrder.RANDOM
import java.io.File
import java.sql.Connection
import java.time.Instant

class SqlLiteFile private constructor(fileName: String) : WorderDB, WorderUpdateDB, WorderInsertDB {
    companion object {
        private const val UPDATED_TAG = "(W) Updated"
        private const val INSERTED_TAG = "(W) Inserted"
        private const val RESET_TAG = "(W) Reset"
        private const val LANG_ID = 2


        fun createInstance(fileName: String): WorderDB = SqlLiteFile(fileName)


        private object DictionaryTable : IntIdTable("Dictionary") {
            val langId: Column<Int> = integer("lang_id")
        }

        private object TagsTable : IntIdTable("Tags") {
            val dictionaryId: Column<Int> = integer("dictionary_id")
            val name: Column<String> = text("name")

            @Suppress("UNUSED")
            val date: Column<Int> = integer("date").default(0)

            @Suppress("UNUSED")
            val lastAddToWord: Column<Int> = integer("last_add_to_word").default(0)

            @Suppress("UNUSED")
            val openShare: Column<Int> = integer("open_share").default(0)
        }

        private object WordTable : IntIdTable("Word") {
            val name: Column<String> = text("name")
            val tags: Column<String?> = text("tags").nullable()
            val translation: Column<String?> = text("translation").nullable()
            val translationAddition: Column<String?> = text("translation_addition").nullable()
            val transcription: Column<String> = text("transcription").default("")
            val example: Column<String?> = text("example").nullable()
            val exampleTranslation: Column<String?> = text("example_translation").nullable()
            val dictionaryId: Column<Int> = integer("dictionary_id")
            val rate: Column<Int> = integer("rate").default(0)
            val register: Column<Long> = long("register").default(Instant.now().toEpochMilli())
            val closed: Column<Int?> = integer("closed").nullable()
            val lastModification: Column<Long> = long("last_modification").default(Instant.now().toEpochMilli())
            val lastRateModification: Column<Long> = long("last_rate_modification").default(Instant.now().toEpochMilli())
            val lastTraining: Column<Int> = integer("last_training").default(0)
        }
    }


    init {
        File(fileName).run {
            if (!exists())
                throw IllegalArgumentException("File not found! file_path=${absolutePath}")
            if (!(canRead() && canWrite()))
                throw IllegalArgumentException("File should be permissible for reading and writing! file_path=${absolutePath}")
        }
    }


    private val sqlLiteCfg = SQLiteConfig().apply {
        setJournalMode(SQLiteConfig.JournalMode.OFF)
        transactionMode = IMMEDIATE
    }
    private val connection: Database = Database.connect({
        sqlLiteCfg.createConnection("jdbc:sqlite:$fileName")
    })
    private val dictionaryId: Int = defaultSqlLiteTransaction {
        DictionaryTable.select { DictionaryTable.langId eq LANG_ID }.firstOrNull()?.get(DictionaryTable.id)
                ?: throw IllegalArgumentException("There's no an English dictionary (LANG_ID: $LANG_ID) in the Database!")
    }.value
    private val updatedTagId: Int = getTagId(INSERTED_TAG)
    private val insertedTagId: Int = getTagId(RESET_TAG)
    private val resetTagId: Int = getTagId(UPDATED_TAG)
    private val skippedWords = mutableListOf<String>()

    override val trackStats = BaseWorderTrackStats().apply {
        totalInserted = getWordsCount(tagId = insertedTagId)
        totalReset = getWordsCount(tagId = resetTagId)
        totalUpdated = getWordsCount(tagId = updatedTagId)
        totalAffected = totalInserted + totalReset + totalUpdated
    }
    override val summaryStats = BaseWorderSummaryStats().apply {
        val totalColumn = WordTable.id.count()

        val learnedColumn = Sum(
                case().When(WordTable.rate eq 100, intParam(1)).Else(intParam(0)),
                IntegerColumnType()
        )

        val unlearnedColumn = Sum(
                case().When(WordTable.rate neq 100, intParam(1)).Else(intParam(0)),
                IntegerColumnType()
        )

        val resultRow = defaultSqlLiteTransaction {
            WordTable.slice(totalColumn, learnedColumn, unlearnedColumn)
                    .selectAll()
                    .first()
        }

        learned = resultRow[learnedColumn]!!
        unlearned = resultRow[unlearnedColumn]!!
        totalAmount = resultRow[totalColumn].toInt()
    }
    override val inserterSessionStats = BaseInserterSessionStats()
    override val updaterSessionStats = BaseUpdaterSessionStats()

    override val inserter: WorderInsertDB = this
    override val updater: WorderUpdateDB = this


    /*
    Private inner/common methods
     */

    private suspend fun <T> suspendedSqlLiteTransaction(statement: suspend Transaction.() -> T): T = newSuspendedTransaction(
            context = Dispatchers.Default,
            db = connection,
            statement = statement
    )

    private fun <T> defaultSqlLiteTransaction(statement: Transaction.() -> T): T = transaction(
            transactionIsolation = Connection.TRANSACTION_SERIALIZABLE,
            repetitionAttempts = 1,
            db = connection,
            statement = statement
    )

    private fun getTagId(tagName: String) = defaultSqlLiteTransaction {
        TagsTable.select { (TagsTable.name eq tagName) and (TagsTable.dictionaryId eq dictionaryId) }.firstOrNull()?.get(TagsTable.id)
                ?: TagsTable.insert {
                    it[dictionaryId] = this@SqlLiteFile.dictionaryId
                    it[name] = tagName
                }[TagsTable.id]
    }.value

    private fun getWordsCount(tagId: Int) = defaultSqlLiteTransaction {
        WordTable.select { (WordTable.tags like "%$tagId%") and (WordTable.dictionaryId eq dictionaryId) }
                .count()
                .toInt()
    }


    /*
    WorderUpdaterDB's Methods Implementation
     */

    private suspend fun selectNext() = suspendedSqlLiteTransaction {
        WordTable.slice(WordTable.columns.drop(2) + WordTable.name.lowerCase()).select {
            (WordTable.tags notLike "%$updatedTagId%" or WordTable.tags.isNull()) and
                    (WordTable.rate less 100) and
                    (WordTable.name.notInList(skippedWords)) and
                    (WordTable.dictionaryId eq dictionaryId)
        }.limit(1)
    }


    override suspend fun hasNextWord(): Boolean = suspendedSqlLiteTransaction { !(selectNext().empty()) }

    override suspend fun getNextWord(order: SelectOrder): DatabaseWord = suspendedSqlLiteTransaction {
        when (order) {
            ASC, DESC -> selectNext().orderBy(WordTable.id, SortOrder.valueOf(order.name))
            RANDOM -> selectNext().orderBy(Random())
        }.firstOrNull()?.let {
            BaseDatabaseWord(
                    name = it[WordTable.name.lowerCase()],
                    transcription = it[WordTable.transcription],
                    rate = it[WordTable.rate],
                    register = it[WordTable.register],
                    lastModification = it[WordTable.lastModification],
                    lastRateModification = it[WordTable.lastRateModification],
                    lastTraining = it[WordTable.lastTraining],
                    examples = it[WordTable.example]?.split("#")?.filter(String::isNotBlank)?.toSet() ?: emptySet(),
                    translations = run {
                        val translations = it[WordTable.translation]?.split("#")?.filter(String::isNotBlank)
                                ?: emptyList()
                        val translationAdditions = it[WordTable.translationAddition]?.split("#")?.filter(String::isNotBlank)
                                ?: emptyList()
                        (translations + translationAdditions).toSet()
                    }
            )
        } ?: throw IllegalStateException("Last call of hasNextWord() returned FALSE!")
    }

    override suspend fun updateWord(word: UpdatedWord) {
        @Suppress("UNCHECKED_CAST")
        fun resolveTagId(tagId: Int) = case()
                .When(WordTable.tags like "%$tagId%", WordTable.tags)
                .When(WordTable.tags like "%#", Concat("", WordTable.tags as Column<String>, stringLiteral("$tagId#")))
                .Else(stringLiteral("#$tagId#"))

        suspendedSqlLiteTransaction {
            WordTable.update({ (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) }) {
                it[translation] = word.primaryDefinition
                it[translationAddition] = word.secondaryDefinition
                it[exampleTranslation] = null
                it[tags] = resolveTagId(updatedTagId)

                it[transcription] = case()
                        .When(stringLiteral(word.transcription ?: "NULL") eq "NULL", transcription)
                        .Else(stringLiteral(word.transcription ?: "NULL"))

                val exampleStr = stringLiteral(word.examples.joinToString("#"))
                it[example] = case()
                        .When(exampleStr eq stringLiteral(""), example)
                        .Else(exampleStr)
            }
        }

        trackStats.apply {
            totalAffected++
            totalUpdated++
        }

        updaterSessionStats.apply {
            totalProcessed++
            updated++
        }
    }

    override suspend fun removeWord(word: Word) {
        suspendedSqlLiteTransaction {
            WordTable.deleteWhere { (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) }
        }

        summaryStats.apply {
            totalAmount--
            unlearned--
        }

        updaterSessionStats.apply {
            totalProcessed++
            removed++
        }
    }

    override suspend fun setAsSkipped(word: Word) {
        skippedWords.add(skippedWords.size, word.name)

        updaterSessionStats.apply {
            totalProcessed++
            skipped++
        }
    }

    override suspend fun setAsLearned(word: Word) {
        suspendedSqlLiteTransaction {
            WordTable.update({ (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) })
            {
                it[rate] = 100
                it[closed] = 1
            }
        }

        summaryStats.apply {
            learned++
            unlearned--
        }

        updaterSessionStats.apply {
            totalProcessed++
            learned++
        }
    }


    /*
    WorderInserterDB's Methods Implementation
     */

    override suspend fun containsWord(name: String): Boolean = suspendedSqlLiteTransaction {
        WordTable.select((WordTable.name eq name) and (WordTable.dictionaryId eq dictionaryId))
                .count()
    } > 0

    override suspend fun insertWord(name: String): Boolean {
        if (containsWord(name))
            return false

        suspendedSqlLiteTransaction {
            WordTable.insert {
                it[WordTable.name] = name
                it[dictionaryId] = dictionaryId
                it[tags] = "#$insertedTagId#"
            }
        }

        trackStats.apply {
            totalAffected++
            totalInserted++
        }

        summaryStats.apply {
            totalAmount++
            unlearned++
        }

        inserterSessionStats.apply {
            totalProcessed++
            inserted++
        }

        return true
    }

    override suspend fun resetWord(name: String): Boolean {
        val updatedRowsCount = suspendedSqlLiteTransaction {
            WordTable.update({ (WordTable.name eq name) and (WordTable.dictionaryId eq dictionaryId) })
            {
                it[rate] = 0
                it[closed] = null

                @Suppress("UNCHECKED_CAST")
                it[tags] = case()
                        .When(tags like "%$resetTagId%", tags)
                        .When(tags like "%#", Concat("", tags as Column<String>, stringLiteral("$resetTagId#")))
                        .Else(stringLiteral("#$resetTagId#"))
            }
        }

        trackStats.apply {
            totalAffected++
            totalReset++
        }

        summaryStats.apply {
            unlearned++
            learned--
        }

        inserterSessionStats.apply {
            totalProcessed++
            reset++
        }

        return updatedRowsCount > 0
    }


    /*
    Any's Methods Overriding
     */

    override fun toString(): String = connection.url.substringAfterLast("/")
}
