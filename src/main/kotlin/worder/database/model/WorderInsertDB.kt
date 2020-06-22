package worder.database.model

import worder.core.model.BareWord

interface WorderInsertDB {
    val inserterStats: InserterStats


    suspend fun containsWord(bareWord: BareWord): Boolean

    suspend fun insertWord(bareWord: BareWord): Boolean

    suspend fun resetWord(bareWord: BareWord): Boolean

    suspend fun resolveWord(bareWord: BareWord): ResolveRes {
        return if (containsWord(bareWord)) {
            resetWord(bareWord)
            ResolveRes.RESET
        } else {
            insertWord(bareWord)
            ResolveRes.INSERTED
        }
    }


    enum class ResolveRes {
        INSERTED, RESET
    }
}
