package worder.database.sqllite

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.case
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import worder.database.*
import worder.database.sqllite.SqlLiteFile.Companion.WordTable


class SqlLiteFileExtracter(fileName: String) : SqlLiteFile(fileName), LocalWordsExtractDb {
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

        return updatedRowsCount > 0
    }
}
