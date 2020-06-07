package worder.model.database

import worder.model.BareWord

interface WorderUpdateDB {
    val updaterSessionStats: UpdaterSessionStats


    suspend fun hasNextWord(): Boolean

    suspend fun getNextWord(order: SelectOrder): DatabaseWord

    suspend fun updateWord(updatedWord: UpdatedWord)

    suspend fun removeWord(word: BareWord)

    suspend fun setAsSkipped(word: BareWord)

    suspend fun setAsLearned(word: BareWord)


    enum class SelectOrder {
        ASC, DESC, RANDOM
    }
}
