package worder.database.sqllite

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import worder.database.UpdaterSessionStat
import worder.database.WordsUpdateDB
import worder.database.WordsUpdateDB.SelectOrder
import worder.database.sqllite.SqlLiteFile.Companion.WordTable
import worder.model.BaseDatabaseWord
import worder.model.Word
import worder.model.DatabaseWord
import worder.model.UpdatedWord

class SqlLiteFileUpdater(fileName: String) : SqlLiteFile(fileName), WordsUpdateDB {
    private val skippedWords = mutableListOf<String>()
    private val selectQuery = defaultSqlLiteTransaction {
        addLogger(StdOutSqlLogger)
        WordTable.slice(WordTable.columns.drop(2) + WordTable.name.lowerCase()).select {
            (WordTable.tags notLike "%$updatedTagId%" or WordTable.tags.isNull()) and
                    (WordTable.rate less 100) and
                    (WordTable.name.notInList(skippedWords)) and
                    (WordTable.dictionaryId eq dictionaryId)
        }.limit(1)
    }

    private var removed = 0
    private var updated = 0
    private var skipped = 0
    private var learned = 0


    override val sessionStat: UpdaterSessionStat
        get() = UpdaterSessionStat(
            removed = removed,
            updated = updated,
            skipped = skipped,
            learned = learned
        )


    override fun hasNextWord() = defaultSqlLiteTransaction { !(selectQuery.empty()) }

    override fun setSkipped(word: Word) = skippedWords.add(skippedWords.size, word.name).also { skipped++ }

    override fun getNextWord(order: SelectOrder): DatabaseWord = defaultSqlLiteTransaction {
            when (order) {
                SelectOrder.ASC, SelectOrder.DESC -> selectQuery.orderBy(WordTable.id, SortOrder.valueOf(order.name))
                SelectOrder.RANDOM -> selectQuery.orderBy(Random())
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
                        val translations = it[WordTable.translation]?.split("#")?.filter(String::isNotBlank) ?: emptyList()
                        val translationAdditions = it[WordTable.translationAddition]?.split("#")?.filter(String::isNotBlank) ?: emptyList()
                        (translations + translationAdditions).toSet()
                    }
                )
            } ?: throw IllegalStateException("Last call of hasNextWord() returned FALSE!")
        }

    override fun updateWord(word: UpdatedWord) {
        val tagsCase = CaseWhen<String?>(null)
            .When(WordTable.tags like "%$updatedTagId%", WordTable.tags)
            .When(WordTable.tags like "%#", Concat("", WordTable.tags as Column<String>, stringLiteral("$updatedTagId#")))
            .Else(stringLiteral("#$updatedTagId#"))
        val exampleStr = stringLiteral(word.examples.joinToString("#"))
        val exampleCase = CaseWhen<String?>(null)
            .When(exampleStr eq stringLiteral(""), WordTable.example)
            .Else(exampleStr)
        val transcriptionCase = CaseWhen<String?>(null)
            .When(stringLiteral(word.transcription ?: "NULL") eq "NULL", WordTable.transcription)
            .Else(stringLiteral(word.transcription ?: "NULL"))

        defaultSqlLiteTransaction {
            addLogger(StdOutSqlLogger)
            WordTable.update({ (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) }) {
                it[transcription] = transcriptionCase
                it[translation] = word.primaryDefinition
                it[translationAddition] = word.secondaryDefinition
                it[exampleTranslation] = null
                it[example] = exampleCase
                it[tags] = tagsCase
            }
        }
    }

    override fun removeWord(word: Word): Unit = defaultSqlLiteTransaction {
        WordTable.deleteWhere { (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) }
    }

    override fun setLearned(word: Word): Unit = defaultSqlLiteTransaction {
        WordTable.update({ (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) })
        {
            it[rate] = 100
            it[closed] = 1
        }
    }
}
