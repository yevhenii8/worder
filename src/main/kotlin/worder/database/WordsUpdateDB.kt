package worder.database

import worder.model.*


interface WordsUpdateDB : WordsDB {
    override val sessionStat: UpdaterSessionStat


    fun hasNextWord() : Boolean

    fun getNextWord(order: SelectOrder): DatabaseWord

    fun updateWord(word: UpdatedWord)

    fun removeWord(word: Word)

    fun setSkipped(word: Word)

    fun setLearned(word: Word)


    enum class SelectOrder {
        ASC, DESC, RANDOM
    }
}
