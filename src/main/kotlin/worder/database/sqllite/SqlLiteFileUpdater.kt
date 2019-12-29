package worder.database.sqllite

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import worder.database.UpdaterSessionStat
import worder.database.WordsUpdateDB
import worder.database.sqllite.SqlLiteFile.Companion.WordTable
import worder.model.Word
import worder.model.DatabaseWord
import worder.model.UpdatedWord

class SqlLiteFileUpdater(fileName: String) : SqlLiteFile(fileName), WordsUpdateDB {
    private val skippedWords = mutableListOf<String>()

    private var nextWord: DatabaseWord? = null
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


    override fun hasNextWord(): Boolean {
        nextWord = defaultSqlLiteTransaction {
            WordTable.slice(WordTable.columns.drop(2) + WordTable.name.lowerCase()).select {
                (WordTable.tags notLike "%$updatedTagId%" or WordTable.tags.isNull()) and
                        (WordTable.rate less 100) and
                        (WordTable.name.notInList(skippedWords)) and
                        (WordTable.dictionaryId eq dictionaryId)
            }
                .orderBy(WordTable.id, SortOrder.ASC)
                .limit(1)
                .firstOrNull()
                ?.let { row ->
                    DatabaseWord(
                        name = row[WordTable.name.lowerCase()],
                        transcription = row[WordTable.transcription],
                        rate = row[WordTable.rate],
                        register = row[WordTable.register],
                        lastModification = row[WordTable.lastModification],
                        lastRateModification = row[WordTable.lastRateModification],
                        lastTraining = row[WordTable.lastTraining]
                    ).apply {
                        row[WordTable.translation]?.let {
                            translations.addAll(it.split("#").filter(String::isNotBlank))
                        }
                        row[WordTable.translationAddition]?.let {
                            translations.addAll(it.split("#").filter(String::isNotBlank))
                        }
                        row[WordTable.example]?.let {
                            examples.addAll(it.split("#").filter(String::isNotBlank))
                        }
                    }
                }
        }

        return nextWord != null
    }

    override fun setSkipped(word: Word) = skippedWords.add(skippedWords.size, word.name).also { skipped++ }

    override fun getNextWord(order: SortOrder) = nextWord ?: throw IllegalStateException("hasNextWord() wasn't called or returned FALSE!")

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
