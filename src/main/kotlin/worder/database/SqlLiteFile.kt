package worder.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.transactions.transaction
import worder.model.Word
import java.sql.Connection
import java.time.Instant

class SqlLiteFile(fileName: String) : WordsDB {
    companion object {
        private const val UPDATED_TAG = "(W) Updated"
        private const val INSERTED_TAG = "(W) Inserted"
        private const val RESET_TAG = "(W) Reset"
        private const val LANG_ID = 2


        private object DictionaryTable : IntIdTable("Dictionary") {
            val langId: Column<Int> = integer("lang_id")
        }

        private object TagsTable : IntIdTable("Tags") {
            val dictionaryId: Column<Int> = integer("dictionary_id")
            val name: Column<String> = text("name")
            val date: Column<Int> = integer("date").default(0)
            val lastAddToWord: Column<Int> = integer("last_add_to_word").default(0)
            val openShare: Column<Int> = integer("open_share").default(0)
        }

        private object WordTable : IntIdTable("Word") {
            val name: Column<String> = text("name")
            val tags: Column<String> = text("tags")
            val primaryDefinition: Column<String> = text("translation")
            val secondaryDefinition: Column<String> = text("translation_addition")
            val transcription: Column<String> = text("transcription")
            val example: Column<String> = text("example")
            val dictionaryId: Column<Int> = integer("dictionary_id")
            val rate: Column<Int> = integer("rate").default(0)
            val register: Column<Long> = long("register").default(Instant.now().toEpochMilli())
            val lastModification: Column<Long> = long("last_modification").default(Instant.now().toEpochMilli())
            val lastRateModification: Column<Long> = long("last_rate_modification").default(Instant.now().toEpochMilli())
            val last_training: Column<Int> = integer("register").default(0)
        }
    }


    private var _removed = 0
    private var _updated = 0
    private var _skipped = 0

    private val _dictionaryId: Int
    private val _updatedTagId: Int
    private val _insertedTagId: Int
    private val _resetTagId: Int


    init {
        Database.connect("jdbc:sqlite:$fileName", "org.sqlite.JDBC")

        _dictionaryId = transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
            DictionaryTable.select { DictionaryTable.langId eq LANG_ID }.firstOrNull()?.get(DictionaryTable.id)
                ?: throw IllegalArgumentException("There's no an English dictionary (LANG_ID: $LANG_ID) in the Database!")
        }.value

        _insertedTagId = getTagId(INSERTED_TAG)
        _resetTagId = getTagId(RESET_TAG)
        _updatedTagId = getTagId(UPDATED_TAG)
    }


    override val sessionStat: DbSessionStat
        get() = DbSessionStat(
            removed = _removed,
            updated = _updated,
            skipped = _skipped
        )

    override val worderTrack: DbWorderTrack
        get() = DbWorderTrack(
            totalInserted = getWordsCount(tagId = _insertedTagId),
            totalReset = getWordsCount(tagId = _resetTagId),
            totalUpdated = getWordsCount(tagId = _updatedTagId)
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

            val resultRow = transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
                WordTable.slice(total, learned, unlearned)
                    .selectAll()
                    .first()
            }

            return DbSummary(
                total = resultRow[total],
                learned = resultRow[learned]!!,
                unlearned = resultRow[unlearned]!!
            )
        }


    override fun getNextWord(order: WordsDB.SelectOrder): Word {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateWord(word: Word) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun ignoreWord(word: Word) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeWord(word: Word) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun getTagId(tagName: String): Int {
        return transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
            TagsTable.select { (TagsTable.name eq tagName) and (TagsTable.dictionaryId eq _dictionaryId) }.firstOrNull()?.get(
                TagsTable.id
            )
                ?: TagsTable.insert {
                    it[dictionaryId] = _dictionaryId
                    it[name] = tagName
                }[TagsTable.id]
        }.value
    }

    private fun getWordsCount(tagId: Int): Int {
        return transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
            WordTable.select { WordTable.tags like "%$tagId%" }.count()
        }
    }
}
