/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <SQLiteFile.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <24/07/2020, 10:32:48 PM>
 * Version: <49>
 */

package worder.database.model.impl

import kotlinx.coroutines.ExecutorCoroutineDispatcher
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Concat
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.Query
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
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.sqlite.SQLiteConfig
import org.sqlite.SQLiteConfig.TransactionMode.EXCLUSIVE
import worder.core.model.BareWord
import worder.core.model.applyWithMainUI
import worder.database.model.DatabaseWord
import worder.database.model.UpdatedWord
import worder.database.model.WorderDB
import worder.database.model.WorderInsertDB
import worder.database.model.WorderUpdateDB
import worder.database.model.WorderUpdateDB.SelectOrder
import worder.database.model.WorderUpdateDB.SelectOrder.ASC
import worder.database.model.WorderUpdateDB.SelectOrder.DESC
import worder.database.model.WorderUpdateDB.SelectOrder.RANDOM
import java.io.File
import java.sql.Connection
import java.time.Instant
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SQLiteFile private constructor(file: File) : WorderDB, WorderUpdateDB, WorderInsertDB {
    companion object {
        private const val UPDATED_TAG = "(W) Updated"
        private const val INSERTED_TAG = "(W) Inserted"
        private const val RESET_TAG = "(W) Reset"
        private const val LANG_ID = 2


        fun createInstance(file: File): WorderDB = SQLiteFile(file)


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
            val lastTraining: Column<Long> = long("last_training").default(0)
        }
    }


    init {
        file.run {
            require(exists()) {
                "File not found! file_path=${absolutePath}"
            }

            require(canRead()) {
                "File should be permissible for reading! file_path=${absolutePath}"
            }

            require(canWrite()) {
                "File should be permissible for writing! file_path=${absolutePath}"
            }
        }
    }


    private val sqliteExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private val sqliteContext: ExecutorCoroutineDispatcher = sqliteExecutor.asCoroutineDispatcher()
    private val sqlLiteCfg: SQLiteConfig = SQLiteConfig().apply {
        setJournalMode(SQLiteConfig.JournalMode.OFF)
        transactionMode = EXCLUSIVE
    }
    private val connection: Database = Database.connect({
        sqlLiteCfg.createConnection("jdbc:sqlite:${file.absolutePath}")
    }).also {
        // SQLite JDBC driver supports only this IsolationLevel
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
    }
    private val skippedWords: MutableList<String> = mutableListOf()

    private var dictionaryId = -1
    private var updatedTagId = -1
    private var insertedTagId = -1
    private var resetTagId = -1

    // track internal stats
    private var totalInserted = -1
    private var totalReset = -1
    private var totalUpdated = -1

    // summary internal stats
    private var learned = -1
    private var unlearned = -1
    private var totalAmount = -1


    /**
     * WorderDB's properties implementation
     */

    override val observableTrackStats: SimpleWorderTrackStats = SimpleWorderTrackStats()
    override val observableSummaryStats: SimpleWorderSummaryStats = SimpleWorderSummaryStats()
    override val observableInserterStats: SimpleInserterStats = SimpleInserterStats()
    override val observableUpdaterStats: SimpleUpdaterStats = SimpleUpdaterStats()

    override val inserter: WorderInsertDB = this
    override val updater: WorderUpdateDB = this


    init {
        var dictionaryIdTmp = -1
        var updatedTagIdTmp = -1
        var insertedTagIdTmp = -1
        var resetTagIdTmp = -1

        sqlLiteTransaction {
            dictionaryIdTmp = sqlLiteTransaction {
                val resultRow = DictionaryTable.select { DictionaryTable.langId eq LANG_ID }.firstOrNull()

                requireNotNull(resultRow) {
                    "There's no an English dictionary (LANG_ID: $LANG_ID) in the Database!"
                }

                resultRow[DictionaryTable.id].value
            }
            updatedTagIdTmp = requestTagIdTxn(INSERTED_TAG)
            insertedTagIdTmp = requestTagIdTxn(RESET_TAG)
            resetTagIdTmp = requestTagIdTxn(UPDATED_TAG)
        }

        dictionaryId = dictionaryIdTmp
        updatedTagId = updatedTagIdTmp
        insertedTagId = insertedTagIdTmp
        resetTagId = resetTagIdTmp

        sqlLiteTransaction {
            requestSummaryStatsTxn()
            requestTrackStatsTxn()
        }

        observableSummaryStats.updateSummaryStats()
        observableTrackStats.updateTrackStats()
    }


    private suspend fun <T> sqlLiteTransactionAsync(statement: suspend Transaction.() -> T): T = newSuspendedTransaction(
            context = sqliteContext,
            db = connection,
            statement = statement
    )

    private fun <T> sqlLiteTransaction(statement: Transaction.() -> T): T = transaction(
            db = connection,
            statement = statement
    )

    private fun SimpleWorderTrackStats.updateTrackStats() {
        totalInserted = this@SQLiteFile.totalInserted
        totalReset = this@SQLiteFile.totalReset
        totalUpdated = this@SQLiteFile.totalUpdated
        totalAffected = totalInserted + totalReset + totalUpdated
    }

    private fun SimpleWorderSummaryStats.updateSummaryStats() {
        learned = this@SQLiteFile.learned
        unlearned = this@SQLiteFile.unlearned
        totalAmount = this@SQLiteFile.totalAmount
    }

    private fun requestTrackStatsTxn() {
        totalInserted = requestWordsCountTxn(insertedTagId).toInt()
        totalReset = requestWordsCountTxn(resetTagId).toInt()
        totalUpdated = requestWordsCountTxn(updatedTagId).toInt()
    }

    private fun requestSummaryStatsTxn() {
        val totalColumn = WordTable.id.count()

        val learnedColumn = Sum(
                case().When(WordTable.rate eq 100, intParam(1)).Else(intParam(0)),
                IntegerColumnType()
        )

        val unlearnedColumn = Sum(
                case().When(WordTable.rate neq 100, intParam(1)).Else(intParam(0)),
                IntegerColumnType()
        )

        val resultRow = WordTable.slice(totalColumn, learnedColumn, unlearnedColumn)
                .selectAll()
                .first()

        learned = resultRow[learnedColumn]!!
        unlearned = resultRow[unlearnedColumn]!!
        totalAmount = resultRow[totalColumn].toInt()
    }

    private fun requestWordsCountTxn(tagId: Int): Long = WordTable.select {
        (WordTable.tags like "%$tagId%") and (WordTable.dictionaryId eq dictionaryId)
    }.count()

    private fun requestTagIdTxn(tagName: String): Int {
        val res = TagsTable.select { (TagsTable.name eq tagName) and (TagsTable.dictionaryId eq dictionaryId) }.firstOrNull()?.get(TagsTable.id)
                ?: TagsTable.insert {
                    it[dictionaryId] = this@SQLiteFile.dictionaryId
                    it[name] = tagName
                }[TagsTable.id]

        return res.value
    }


    /*
    WorderUpdaterDB's Methods Implementation
     */

    private fun selectNextTxn(): Query = WordTable.slice(WordTable.columns.drop(2) + WordTable.name.lowerCase()).select {
        (WordTable.tags notLike "%$updatedTagId%" or WordTable.tags.isNull()) and
                (WordTable.rate less 100) and
                (WordTable.name.notInList(skippedWords)) and
                (WordTable.dictionaryId eq dictionaryId)
    }.limit(1)

    override suspend fun hasNextWord(): Boolean = sqlLiteTransactionAsync { !(selectNextTxn().empty()) }

    override suspend fun getNextWord(order: SelectOrder): DatabaseWord = sqlLiteTransactionAsync {
        val resultRow = when (order) {
            ASC, DESC -> selectNextTxn().orderBy(WordTable.id, SortOrder.valueOf(order.name))
            RANDOM -> selectNextTxn().orderBy(Random())
        }.firstOrNull()

        checkNotNull(resultRow) {
            "Last call of hasNextWord() returned FALSE!"
        }

        DatabaseWord(
                name = resultRow[WordTable.name.lowerCase()],
                transcription = resultRow[WordTable.transcription],
                rate = resultRow[WordTable.rate],
                registered = resultRow[WordTable.register],
                lastModified = resultRow[WordTable.lastModification],
                lastRateModified = resultRow[WordTable.lastRateModification],
                lastTrained = resultRow[WordTable.lastTraining],
                examples = (resultRow[WordTable.example] ?: "").split("#").filter(String::isNotBlank).distinct(),
                translations = run {
                    val translations = (resultRow[WordTable.translation] ?: "").split("#").filter(String::isNotBlank)
                    val translationAdditions = (resultRow[WordTable.translationAddition] ?: "").split("#").filter(String::isNotBlank)
                    (translations + translationAdditions).distinct()
                }
        )
    }

    override suspend fun updateWord(updatedWord: UpdatedWord) {
        @Suppress("UNCHECKED_CAST")
        fun resolveTagId(tagId: Int) = case()
                .When(WordTable.tags like "%$tagId%", WordTable.tags)
                .When(WordTable.tags like "%#", Concat("", WordTable.tags as Column<String>, stringLiteral("$tagId#")))
                .Else(stringLiteral("#$tagId#"))

        sqlLiteTransactionAsync {
            WordTable.update({ (WordTable.name eq updatedWord.name) and (WordTable.dictionaryId eq dictionaryId) }) { wordTable ->
                wordTable[translation] = updatedWord.primaryDefinition
                wordTable[translationAddition] = updatedWord.secondaryDefinition
                wordTable[exampleTranslation] = null
                wordTable[tags] = resolveTagId(updatedTagId)
                wordTable[transcription] = updatedWord.transcription
                wordTable[example] = stringLiteral(updatedWord.examples.joinToString("#")).let {
                    case()
                            .When(it eq stringLiteral(""), example)
                            .Else(it)
                }
            }
        }

        MainScope().launch {
            observableTrackStats.apply {
                totalAffected++
                totalUpdated++
            }

            observableUpdaterStats.apply {
                totalProcessed++
                updated++
            }
        }
    }

    override suspend fun removeWord(bareWord: BareWord) {
        sqlLiteTransactionAsync {
            WordTable.deleteWhere { (WordTable.name eq bareWord.name) and (WordTable.dictionaryId eq dictionaryId) }
        }

        MainScope().launch {
            observableSummaryStats.apply {
                totalAmount--
                unlearned--
            }

            observableUpdaterStats.apply {
                totalProcessed++
                removed++
            }
        }
    }

    override suspend fun setAsSkipped(bareWord: BareWord) {
        synchronized(skippedWords) {
            skippedWords.add(bareWord.name)
        }

        observableUpdaterStats.applyWithMainUI {
            totalProcessed++
            skipped++
        }
    }

    override suspend fun setAsLearned(bareWord: BareWord) {
        sqlLiteTransactionAsync {
            WordTable.update({ (WordTable.name eq bareWord.name) and (WordTable.dictionaryId eq dictionaryId) })
            {
                it[rate] = 100
                it[closed] = 1
            }
        }

        MainScope().launch {
            observableSummaryStats.apply {
                learned++
                unlearned--
            }

            observableUpdaterStats.apply {
                totalProcessed++
                learned++
            }
        }
    }


    /*
    WorderInserterDB's Methods Implementation
     */

    override suspend fun resolveWords(bareWords: Collection<BareWord>): Map<BareWord, WorderInsertDB.ResolveRes> {
        val resolveResult = sqlLiteTransactionAsync {
            val res = bareWords.associateWith { if (containsWordTxn(it)) resetWordTxn(it) else insertWordTxn(it) }

            requestSummaryStatsTxn()
            requestTrackStatsTxn()

            res
        }

        val (reset, inserted) = resolveResult.entries
                .partition { it.value == WorderInsertDB.ResolveRes.RESET }

        MainScope().launch {
            observableSummaryStats.updateSummaryStats()
            observableTrackStats.updateTrackStats()

            observableInserterStats.apply {
                this.inserted += inserted.size
                this.reset += reset.size
                this.totalProcessed += resolveResult.size
            }
        }



        return resolveResult
    }

    private fun containsWordTxn(bareWord: BareWord): Boolean =
            WordTable.select((WordTable.name eq bareWord.name) and (WordTable.dictionaryId eq dictionaryId)).count() > 0

    private fun insertWordTxn(bareWord: BareWord): WorderInsertDB.ResolveRes {
        WordTable.insert {
            it[name] = bareWord.name
            it[dictionaryId] = this@SQLiteFile.dictionaryId
            it[tags] = "#$insertedTagId#"
        }

        return WorderInsertDB.ResolveRes.INSERTED
    }

    private fun resetWordTxn(bareWord: BareWord): WorderInsertDB.ResolveRes {
        WordTable.update({ (WordTable.name eq bareWord.name) and (WordTable.dictionaryId eq dictionaryId) })
        {
            it[rate] = 0
            it[closed] = null

            @Suppress("UNCHECKED_CAST")
            it[tags] = case()
                    .When(tags like "%$resetTagId%", tags)
                    .When(tags like "%#", Concat("", tags as Column<String>, stringLiteral("$resetTagId#")))
                    .Else(stringLiteral("#$resetTagId#"))
        }

        return WorderInsertDB.ResolveRes.RESET
    }


    /*
    Other Methods Overriding
     */

    override fun close() {
        sqliteExecutor.shutdown()
    }

    override fun toString(): String = connection.url
}
