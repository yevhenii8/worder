package worder.model.insert

import worder.model.Stats

interface InsertBatchStats : Stats {
    val batchStatus: InsertBatchStatus

    val totalProcessed: Int
    val validProcessed: Int
    val invalidProcessed: Int
    val reset: Int
    val inserted: Int

    val spentTime: String
    val progressBar: String

    enum class InsertBatchStatus {
        READY_TO_PROCESS, PARTIALLY_COMMITTED, COMMITTED
    }
}

interface InsertUnitStats : Stats {
    val fileName: String
    val fileSize: Long
    val status: InsertUnitStatus

    enum class InsertUnitStatus {
        READY_TO_PROCESS, PROCESSING, COMMITTED, EXCLUDED_FROM_BATCH
    }
}
