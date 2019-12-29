package worder.database

import org.jetbrains.exposed.sql.SortOrder
import worder.model.*

interface WordsUpdateDB : WordsDB {
    override val sessionStat: UpdaterSessionStat


    fun hasNextWord() : Boolean

    fun getNextWord(order: SortOrder): DatabaseWord

    fun updateWord(word: UpdatedWord)

    fun removeWord(word: Word)

    fun setSkipped(word: Word)

    fun setLearned(word: Word)
}
