package worder.database

import worder.DatabaseWord
import worder.UpdatedWord
import worder.Word


interface WordsUpdateDb : WordsDb {
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
