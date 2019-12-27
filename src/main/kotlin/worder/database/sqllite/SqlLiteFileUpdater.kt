package worder.database.sqllite

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.coalesce
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.isNull
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import worder.database.UpdaterSessionStat
import worder.database.WordsUpdateDB
import worder.database.sqllite.SqlLiteFile.Companion.WordTable
import worder.model.*
import java.sql.Connection

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
        nextWord = transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
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

    override fun setSkipped(word: BaseWord) = skippedWords.add(skippedWords.size, word.name).also { skipped++ }
    override fun getNextWord(order: SortOrder) = nextWord
        ?: throw IllegalStateException("hasNextWord() wasn't called or last call returned FALSE!")

    override fun updateWord(word: UpdatedWord) {
        val tagsCase = CaseWhen<String?>(null)
            .When(WordTable.tags like "%$updatedTagId%", WordTable.tags)
            .When(WordTable.tags like "%#", Concat("", WordTable.tags, stringLiteral("$updatedTagId#") as Expression<String?>))
            .Else(stringParam("#$updatedTagId#"))
        val exampleStr = stringLiteral(word.examples.joinToString("#"))
        val exampleCase = CaseWhen<String?>(null)
            .When(exampleStr eq stringLiteral(""), WordTable.example)
            .Else(exampleStr)
        val transcriptionCase = CaseWhen<String?>(WordTable.tags)
            .When(stringLiteral(word.transcription ?: "NULL") eq "NULL", WordTable.tags)
            .Else(stringLiteral(word.transcription ?: "NULL"))


        transaction(Connection.TRANSACTION_SERIALIZABLE, 1) {
            addLogger(StdOutSqlLogger)
            WordTable.update({ (WordTable.name eq word.name) and (WordTable.dictionaryId eq dictionaryId) }) {
                //it[transcription] = transcriptionCase
                it[transcription] = Coalesce(stringParam(word.transcription), transcription)
                it[translation] = word.primaryDefinition
                it[translationAddition] = word.secondaryDefinition
                it[exampleTranslation] = null
                it[example] = exampleCase
                it[tags] = tagsCase
            }
        }
    }

    override fun removeWord(word: BaseWord) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setLearned(word: BaseWord) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
