package worder.database.sqllite

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import worder.database.InserterSessionStat
import worder.database.WordsInsertDB
import worder.database.sqllite.SqlLiteFile.Companion.WordTable
import worder.model.Word

class SqlLiteFileInserter(fileName: String) : SqlLiteFile(fileName), WordsInsertDB {
    private var inserted = 0
    private var reset = 0


    override val sessionStat: InserterSessionStat
        get() = InserterSessionStat(
            inserted = inserted,
            reset = reset
        )


    override fun containsWord(word: Word) =
        defaultSqlLiteTransaction { WordTable.select((WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId)).count() } > 0

    override fun insertWord(word: Word): Unit = defaultSqlLiteTransaction {
        WordTable.insert {
            it[name] = word.name
            it[transcription] = word.transcription
            it[dictionaryId] = super.dictionaryId
            it[tags] = "#$insertedTagId#"
        }
    }

    override fun resetWord(word: Word) {
        val tagsCase = CaseWhen<String?>(null)
            .When(WordTable.tags like "%$resetTagId%", WordTable.tags)
            .When(WordTable.tags like "%#", Concat("", WordTable.tags as Column<String>, stringLiteral("$resetTagId#")))
            .Else(stringLiteral("#$resetTagId#"))

        defaultSqlLiteTransaction {
            WordTable.update({ (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) })
            {
                it[tags] = tagsCase
                it[rate] = 0
                it[closed] = null
            }
        }
    }
}
