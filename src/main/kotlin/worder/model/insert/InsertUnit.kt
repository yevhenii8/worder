package worder.model.insert

import worder.model.BareWord

interface InsertUnit {
    val id: String
    val status: InsertUnitStatus
    val unitStats: InsertUnitStats
    val isIncluded: Boolean

    val invalidWords: Set<InvalidWord>
    val validWords: Set<BareWord>

    fun excludeFromBatch()
    fun includeInBatch()

    suspend fun commit()

    enum class InsertUnitStatus {
        READY_TO_COMMIT,
        COMMITTING,
        COMMITTED_SUCCESSFULLY,
        EXCLUDED_FROM_BATCH,
        RESOLUTION_NEEDED,
        COMMITTED_SKIPPED
    }
}

interface InvalidWord {
    val value: String

    fun reject()
    fun substitute(substitution: String): Boolean
}
