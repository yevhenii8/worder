package worder.model.database.implementations

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
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import worder.BaseDatabaseWord
import worder.DatabaseWord
import worder.UpdatedWord
import worder.Word
import worder.model.database.DatabaseSummaryStats
import worder.model.database.DbSummary
import worder.model.database.DbWorderTrack
import worder.model.database.InserterSessionStat
import worder.model.database.UpdaterSessionStat
import worder.model.database.WorderDB
import worder.model.database.WorderInsertDB
import worder.model.database.WorderTrackStats
import worder.model.database.WorderUpdateDB
import worder.model.database.WorderUpdateDB.SelectOrder
import worder.model.database.WorderUpdateDB.SelectOrder.ASC
import worder.model.database.WorderUpdateDB.SelectOrder.DESC
import worder.model.database.WorderUpdateDB.SelectOrder.RANDOM
import java.io.File
import java.sql.Connection
import java.time.Instant

class SqlLiteFile(fileName: String) : WorderDB, WorderUpdateDB, WorderInsertDB {
    private companion object {
        const val UPDATED_TAG = "(W) Updated"
        const val INSERTED_TAG = "(W) Inserted"
        const val RESET_TAG = "(W) Reset"
        const val LANG_ID = 2


        object DictionaryTable : IntIdTable("Dictionary") {
            val langId: Column<Int> = integer("lang_id")
        }

        object TagsTable : IntIdTable("Tags") {
            val dictionaryId: Column<Int> = integer("dictionary_id")
            val name: Column<String> = text("name")
            val date: Column<Int> = integer("date").default(0)
            val lastAddToWord: Column<Int> = integer("last_add_to_word").default(0)
            val openShare: Column<Int> = integer("open_share").default(0)
        }

        object WordTable : IntIdTable("Word") {
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
            val lastTraining: Column<Int> = integer("register").default(0)
        }
    }

    private object SqlLiteSummaryStats : DatabaseSummaryStats {
        override var total: Int = 0
        override val unlearned: Int
            get() = TODO("Not yet implemented")
        override val learned: Int
            get() = TODO("Not yet implemented")
        override val origin: String
            get() = TODO("Not yet implemented")

        override fun subscribe(tracer: DatabaseSummaryStats.() -> Unit) {
            TODO("Not yet implemented")
        }

        override fun unsubscribe(tracer: DatabaseSummaryStats.() -> Unit) {
            TODO("Not yet implemented")
        }

        fun updateSummaryStats() {

        }
    }


    private val connection: Database
    private val dictionaryId: Int
    private val updatedTagId: Int
    private val insertedTagId: Int
    private val resetTagId: Int

    // TODO Stats retrieving and utilizing
    private var inserted = 0
    private var reset = 0

    // TODO Stats retrieving and utilizing
    private var removed = 0
    private var updated = 0
    private var skipped = 0
    private var learned = 0


    init {
        val file = File(fileName)

        if (!file.exists())
            throw IllegalArgumentException("File not found! file_path=${file.absolutePath}")
        if (!(file.canRead() && file.canWrite()))
            throw IllegalArgumentException("File should be permissible for reading and writing! file_path=${file.absolutePath}")

        connection = Database.connect("jdbc:sqlite:$fileName", "org.sqlite.JDBC")
        dictionaryId = defaultSqlLiteTransaction {
            DictionaryTable.select { DictionaryTable.langId eq LANG_ID }.firstOrNull()?.get(DictionaryTable.id)
                    ?: throw IllegalArgumentException("There's no an English dictionary (LANG_ID: $LANG_ID) in the Database!")
        }.value

        insertedTagId = getTagId(INSERTED_TAG)
        resetTagId = getTagId(RESET_TAG)
        updatedTagId = getTagId(UPDATED_TAG)
    }


    // TODO Stats retrieving and utilizing
    override val worderTrack: WorderTrackStats
        get() = DbWorderTrack(
                origin = this.javaClass.simpleName,
                totalInserted = getWordsCount(tagId = insertedTagId),
                totalReset = getWordsCount(tagId = resetTagId),
                totalUpdated = getWordsCount(tagId = updatedTagId)
        )

    // TODO Stats retrieving and utilizing
    override val summaryStat: DatabaseSummaryStats
        get() {
            val total = WordTable.id.count()
            val learned = Sum(
                    case().When(WordTable.rate eq 100, intParam(1)).Else(intParam(0)),
                    IntegerColumnType()
            )
            val unlearned = Sum(
                    case().When(WordTable.rate neq 100, intParam(1)).Else(intParam(0)),
                    IntegerColumnType()
            )

            val resultRow = defaultSqlLiteTransaction {
                WordTable.slice(total, learned, unlearned)
                        .selectAll()
                        .first()
            }

            return DbSummary(
                    origin = this.javaClass.simpleName,
                    total = resultRow[total].toInt(),
                    learned = resultRow[learned]!!,
                    unlearned = resultRow[unlearned]!!
            )
        }

    // TODO Stats retrieving and utilizing
    override val inserterSessionStat: InserterSessionStat
        get() = TODO("Not yet implemented")

    // TODO Stats retrieving and utilizing
    override val updaterSessionStat: UpdaterSessionStat
        get() = TODO("Not yet implemented")


    private val skippedWords = mutableListOf<String>()

    private val selectQuery = defaultSqlLiteTransaction {
        WordTable.slice(WordTable.columns.drop(2) + WordTable.name.lowerCase()).select {
            (WordTable.tags notLike "%$updatedTagId%" or WordTable.tags.isNull()) and
                    (WordTable.rate less 100) and
                    (WordTable.name.notInList(skippedWords)) and
                    (WordTable.dictionaryId eq dictionaryId)
        }.limit(1)
    }


    private fun getTagId(tagName: String) = defaultSqlLiteTransaction {
        TagsTable.select { (TagsTable.name eq tagName) and (TagsTable.dictionaryId eq dictionaryId) }.firstOrNull()?.get(TagsTable.id)
                ?: TagsTable.insert {
                    it[dictionaryId] = dictionaryId
                    it[name] = tagName
                }[TagsTable.id]
    }.value

    private fun getWordsCount(tagId: Int) = defaultSqlLiteTransaction {
        WordTable.select { (WordTable.tags like "%$tagId%") and (WordTable.dictionaryId eq dictionaryId) }
                .count()
                .toInt()
    }

    private fun <T> defaultSqlLiteTransaction(statement: Transaction.() -> T): T = transaction(
            transactionIsolation = Connection.TRANSACTION_SERIALIZABLE,
            repetitionAttempts = 1,
            db = connection,
            statement = statement
    )

    @Suppress("UNCHECKED_CAST")
    private fun resolveTagId(tagId: Int) = case()
            .When(WordTable.tags like "%$tagId%", WordTable.tags)
            .When(WordTable.tags like "%#", Concat("", WordTable.tags as Column<String>, stringLiteral("$tagId#")))
            .Else(stringLiteral("#$tagId#"))

    override fun hasNextWord(): Boolean = defaultSqlLiteTransaction { !(selectQuery.empty()) }

    override fun getNextWord(order: SelectOrder): DatabaseWord = defaultSqlLiteTransaction {
        when (order) {
            ASC, DESC -> selectQuery.orderBy(WordTable.id, SortOrder.valueOf(order.name))
            RANDOM -> selectQuery.orderBy(Random())
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

    override fun updateWord(word: UpdatedWord) {
        defaultSqlLiteTransaction {
            WordTable.update({ (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) }) {
                it[translation] = word.primaryDefinition
                it[translationAddition] = word.secondaryDefinition
                it[exampleTranslation] = null
                it[tags] = resolveTagId(updatedTagId)

                it[transcription] = case()
                        .When(stringLiteral(word.transcription ?: "NULL") eq "NULL", WordTable.transcription)
                        .Else(stringLiteral(word.transcription ?: "NULL"))

                val exampleStr = stringLiteral(word.examples.joinToString("#"))
                it[example] = case()
                        .When(exampleStr eq stringLiteral(""), example)
                        .Else(exampleStr)
            }
        }
    }

    override fun removeWord(word: Word): Unit = defaultSqlLiteTransaction {
        WordTable.deleteWhere { (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) }
    }

    override fun setSkipped(word: Word) = skippedWords.add(skippedWords.size, word.name).also { skipped++ }

    override fun setLearned(word: Word): Unit = defaultSqlLiteTransaction {
        WordTable.update({ (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) })
        {
            it[rate] = 100
            it[closed] = 1
        }
    }

    override fun containsWord(name: String): Boolean =
            defaultSqlLiteTransaction {
                WordTable.select((WordTable.name eq name) and (WordTable.dictionaryId eq dictionaryId))
                        .count()
            } > 0

    override fun addWord(name: String): Boolean {
        if (containsWord(name))
            return false

        defaultSqlLiteTransaction {
            WordTable.insert {
                it[WordTable.name] = name
                it[dictionaryId] = dictionaryId
                it[tags] = "#$insertedTagId#"
            }
        }

        return true
    }

    override fun resetWord(name: String): Boolean {
        val updatedRowsCount = defaultSqlLiteTransaction {
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

        // TODO Example of using
        SqlLiteSummaryStats.updateSummaryStats()

        return updatedRowsCount > 0
    }
}
