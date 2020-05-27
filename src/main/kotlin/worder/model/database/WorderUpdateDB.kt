package worder.model.database

import worder.DatabaseWord
import worder.UpdatedWord
import worder.Word

interface WorderUpdateDB {
    val updaterSessionStats: UpdaterSessionStats


    fun hasNextWord(): Boolean

    fun getNextWord(order: SelectOrder): DatabaseWord

    fun updateWord(word: UpdatedWord)

    fun removeWord(word: Word)

    fun setAsSkipped(word: Word)

    fun setAsLearned(word: Word)


    enum class SelectOrder {
        ASC, DESC, RANDOM
    }
}
