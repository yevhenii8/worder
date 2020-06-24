package worder.database.model

import worder.core.model.BareWord

interface WorderInsertDB {
    val observableInserterStats: ObservableInserterStats

    suspend fun resolveWords(bareWords: Collection<BareWord>): Map<BareWord, ResolveRes>

    enum class ResolveRes {
        INSERTED, RESET
    }
}
