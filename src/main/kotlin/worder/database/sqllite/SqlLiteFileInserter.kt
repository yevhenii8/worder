package worder.database.sqllite

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import worder.database.InserterSessionStat
import worder.database.WordsExtractDb
import worder.database.sqllite.SqlLiteFile.Companion.WordTable

class SqlLiteFileInserter(fileName: String) : SqlLiteFile(fileName), WordsExtractDb {
    private var inserted = 0
    private var reset = 0


    override val sessionStat: InserterSessionStat
        get() = InserterSessionStat(
            origin = this.javaClass.simpleName,
            inserted = inserted,
            reset = reset
        )

    override fun containsWord(name: String): Boolean =
        defaultSqlLiteTransaction { WordTable.select((WordTable.name eq name) and (WordTable.dictionaryId eq dictionaryId)).count() } > 0

    override fun addWord(name: String): Boolean {
        if (containsWord(name))
            return false

        defaultSqlLiteTransaction {
            WordTable.insert {
                it[WordTable.name] = name
                it[dictionaryId] = super.dictionaryId
                it[tags] = "#$insertedTagId#"
            }
        }

        return true
    }

    override fun resetWord(name: String): Boolean {
        val tagsCase = CaseWhen<String?>(null)
            .When(WordTable.tags like "%$resetTagId%", WordTable.tags)
            .When(WordTable.tags like "%#", Concat("", WordTable.tags as Column<String>, stringLiteral("$resetTagId#")))
            .Else(stringLiteral("#$resetTagId#"))

        val updatedRowsCount = defaultSqlLiteTransaction {
            WordTable.update({ (WordTable.name eq name) and (WordTable.dictionaryId eq dictionaryId) })
            {
                it[tags] = tagsCase
                it[rate] = 0
                it[closed] = null
            }
        }

        return updatedRowsCount > 0
    }
}
