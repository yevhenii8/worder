package worder.model.insert

interface InsertBatch {
    val units: List<InsertUnit>
    val batchStats: InsertBatchStats

    suspend fun processBatch()
}
