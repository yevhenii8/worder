package worder.model.insert

import worder.model.Stats
import worder.model.insert.InsertBatch.InsertBatchStatus
import worder.model.insert.InsertUnit.InsertUnitStatus

interface InsertBatchStats : Stats {
    val id: String
    val status: InsertBatchStatus

    val committedUnits: Int
    val totalProcessed: Int
    val validProcessed: Int
    val invalidProcessed: Int
    val reset: Int
    val inserted: Int
}

interface InsertUnitStats : Stats {
    val id: String
    val status: InsertUnitStatus

    val fileName: String
    val fileSize: Long

    val invalidWords: Int
    val validWords: Int
}
