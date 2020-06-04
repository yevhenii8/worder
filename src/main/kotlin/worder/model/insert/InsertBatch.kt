package worder.model.insert

interface InsertBatch {
    val id: String
    val status: InsertBatchStatus
    val batchStats: InsertBatchStats
    val units: List<InsertUnit>

    suspend fun commitAll()

    enum class InsertBatchStatus {
        READY_TO_COMMIT, COMMITTING, PARTIALLY_COMMITTED, COMMITTED
    }
}
