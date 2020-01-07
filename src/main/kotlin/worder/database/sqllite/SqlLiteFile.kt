package worder.database.sqllite

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.transactions.transaction
import worder.database.DbSummary
import worder.database.DbWorderTrack
import worder.database.WordsDb
import java.io.File
import java.sql.Connection
import java.time.Instant

abstract class SqlLiteFile(fileName: String) : WordsDb {
    companion object {
        protected const val UPDATED_TAG = "(W) Updated"
        protected const val INSERTED_TAG = "(W) Inserted"
        protected const val RESET_TAG = "(W) Reset"
        protected const val LANG_ID = 2


        protected object DictionaryTable : IntIdTable("Dictionary") {
            val langId: Column<Int> = integer("lang_id")
        }

        protected object TagsTable : IntIdTable("Tags") {
            val dictionaryId: Column<Int> = integer("dictionary_id")
            val name: Column<String> = text("name")
            val date: Column<Int> = integer("date").default(0)
            val lastAddToWord: Column<Int> = integer("last_add_to_word").default(0)
            val openShare: Column<Int> = integer("open_share").default(0)
        }

        protected object WordTable : IntIdTable("Word") {
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


    private val connection: Database
    protected val dictionaryId: Int
    protected val updatedTagId: Int
    protected val insertedTagId: Int
    protected val resetTagId: Int


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


    override val worderTrack: DbWorderTrack
        get() = DbWorderTrack(
            origin = this.javaClass.simpleName,
            totalInserted = getWordsCount(tagId = insertedTagId),
            totalReset = getWordsCount(tagId = resetTagId),
            totalUpdated = getWordsCount(tagId = updatedTagId)
        )

    override val summary: DbSummary
        get() {
            val total = WordTable.id.count()
            val learned = Sum(
                CaseWhen<Int>(null).When(WordTable.rate eq 100, intParam(1)).Else(intParam(0)),
                LongColumnType()
            )
            val unlearned = Sum(
                CaseWhen<Int>(null).When(WordTable.rate neq 100, intParam(1)).Else(intParam(0)),
                LongColumnType()
            )

            val resultRow = defaultSqlLiteTransaction {
                WordTable.slice(total, learned, unlearned)
                    .selectAll()
                    .first()
            }

            return DbSummary(
                origin = this.javaClass.simpleName,
                total = resultRow[total],
                learned = resultRow[learned]!!,
                unlearned = resultRow[unlearned]!!
            )
        }


    private fun getTagId(tagName: String) = defaultSqlLiteTransaction {
        TagsTable.select { (TagsTable.name eq tagName) and (TagsTable.dictionaryId eq dictionaryId) }.firstOrNull()?.get(TagsTable.id)
            ?: TagsTable.insert {
                it[dictionaryId] = this@SqlLiteFile.dictionaryId
                it[name] = tagName
            }[TagsTable.id]
    }.value

    private fun getWordsCount(tagId: Int) = defaultSqlLiteTransaction { WordTable.select { WordTable.tags like "%$tagId%" }.count() }

    protected fun <T> defaultSqlLiteTransaction(statement: Transaction.() -> T): T = transaction(
        transactionIsolation = Connection.TRANSACTION_SERIALIZABLE,
        repetitionAttempts = 1,
        db = connection,
        statement = statement
    )
}
