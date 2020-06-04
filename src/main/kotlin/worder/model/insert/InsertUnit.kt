package worder.model.insert

interface InsertUnit {
    val id: String
    val status: InsertUnitStatus
    val unitStats: InsertUnitStats
    val isIncluded: Boolean

    val invalidWords: Set<String>
    val validWords: Set<String>

    fun excludeFromBatch()
    fun includeInBatch()

    suspend fun commit()

    enum class InsertUnitStatus {
        READY_TO_COMMIT, COMMITTING, COMMITTED, EXCLUDED_FROM_BATCH
    }
}
