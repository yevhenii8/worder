package worder.database.model

import worder.core.model.BareWord

interface WorderUpdateDB {
    val observableUpdaterStats: ObservableUpdaterStats


    suspend fun hasNextWord(): Boolean

    suspend fun getNextWord(order: SelectOrder): DatabaseWord

    suspend fun updateWord(updatedWord: UpdatedWord)

    suspend fun removeWord(bareWord: BareWord)

    suspend fun setAsSkipped(bareWord: BareWord)

    suspend fun setAsLearned(bareWord: BareWord)


    enum class SelectOrder {
        ASC, DESC, RANDOM
    }
}
