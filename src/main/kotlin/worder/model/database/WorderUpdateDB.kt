package worder.model.database

import worder.DatabaseWord
import worder.UpdatedWord
import worder.Word

interface WorderUpdateDB {
    val updaterSessionStats: UpdaterSessionStats


    suspend fun hasNextWord(): Boolean

    suspend fun getNextWord(order: SelectOrder): DatabaseWord

    suspend fun updateWord(word: UpdatedWord)

    suspend fun removeWord(word: Word)

    suspend fun setAsSkipped(word: Word)

    suspend fun setAsLearned(word: Word)


    enum class SelectOrder {
        ASC, DESC, RANDOM
    }
}
