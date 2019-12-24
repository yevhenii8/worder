package worder.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import worder.model.Word
import java.nio.file.Files
import java.nio.file.Path
import java.sql.Connection

class SqlLiteFile(fileName: String) : WordsDB {
    companion object {
        private const val FILE_EXTENSION = ".bck"
        private const val LANG_ID = 2

        fun isValidSqlLiteDb(file: Path) =
            file.toString().endsWith(FILE_EXTENSION) && Files.isReadable(file) && Files.isWritable(file)

        object Dictionary : Table() {
            val id: Column<Int> = integer("id")
            val langId: Column<Int> = integer("lang_id")
        }

        object Tags : Table() {
            val id: Column<Int> = integer("id").autoIncrement().primaryKey()
            val dictionaryId: Column<Int> = integer("dictionary_id")
            val name: Column<String> = text("name")
        }
    }

    private val _dictionaryId: Int
    private val _updatedTagId: Int
    private val _insertedTagId: Int
    private val _resetTagId: Int

    init {
        Database.connect("jdbc:sqlite:$fileName", "org.sqlite.JDBC")

        _dictionaryId = transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
            Dictionary.select { Dictionary.langId eq LANG_ID }.firstOrNull()?.get(Dictionary.id)
                ?: throw IllegalArgumentException("There's no an English dictionary (LANG_ID: $LANG_ID) in Database!")
        }

        _insertedTagId = transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
            Tags.select { Tags.name eq insertedTag }.firstOrNull()?.get(Tags.id)
                ?: Tags.insert {
                    it[dictionaryId] = _dictionaryId
                    it[name] = insertedTag
                }[Tags.id]
        }

        _resetTagId = transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
            Tags.select { Tags.name eq resetTag }.firstOrNull()?.get(Tags.id)
                ?: Tags.insert {
                    it[dictionaryId] = _dictionaryId
                    it[name] = resetTag
                }[Tags.id]
        }

        _updatedTagId = transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
            Tags.select { Tags.name eq updatedTag }.firstOrNull()?.get(Tags.id)
                ?: Tags.insert {
                    it[dictionaryId] = _dictionaryId
                    it[name] = updatedTag
                }[Tags.id]
        }
    }

    override val usingStat: DbUsingStat
        get() = TODO("not implemented")
    override val summary: DbSummary
        get() = TODO("not implemented")

    override fun getNextWords(count: Int, order: WordsDB.SelectOrder): List<Word> {
        TODO("not implemented")
    }

    override fun updateWords(words: Collection<Word>) {
        TODO("not implemented")
    }

    override fun ignoreWords(words: Collection<Word>) {
        TODO("not implemented")
    }

    override fun removeWords(words: Collection<Word>) {
        TODO("not implemented")
    }
}