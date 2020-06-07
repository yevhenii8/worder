package worder.model.database

import worder.model.BareWord

interface WorderInsertDB {
    val inserterSessionStats: InserterSessionStats


    suspend fun containsWord(word: BareWord): Boolean

    suspend fun insertWord(word: BareWord): Boolean

    suspend fun resetWord(word: BareWord): Boolean

    suspend fun resolveWord(word: BareWord): ResolveRes {
        return if (containsWord(word)) {
            resetWord(word)
            ResolveRes.RESET
        } else {
            insertWord(word)
            ResolveRes.INSERTED
        }
    }


    enum class ResolveRes {
        INSERTED, RESET
    }
}
