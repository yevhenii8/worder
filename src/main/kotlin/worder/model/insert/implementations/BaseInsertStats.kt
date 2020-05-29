package worder.model.insert.implementations

import worder.model.SharedStats
import worder.model.insert.InsertBatchStats
import worder.model.insert.InsertBatchStats.InsertBatchStatus
import worder.model.insert.InsertUnitStats
import worder.model.insert.InsertUnitStats.InsertUnitStatus

class BaseInsertBatchStats(
        origin: String,
        batchStatus: InsertBatchStatus,
        totalProcessed: Int,
        validProcessed: Int,
        invalidProcessed: Int,
        reset: Int,
        inserted: Int,
        spentTime: String,
        progressBar: String
) : InsertBatchStats, SharedStats(origin) {
    override var batchStatus: InsertBatchStatus by SharedStatsBinder.bind(this, batchStatus)
    override var totalProcessed: Int by SharedStatsBinder.bind(this, totalProcessed)
    override var validProcessed: Int by SharedStatsBinder.bind(this, validProcessed)
    override var invalidProcessed: Int by SharedStatsBinder.bind(this, invalidProcessed)
    override var reset: Int by SharedStatsBinder.bind(this, reset)
    override var inserted: Int by SharedStatsBinder.bind(this, inserted)
    override var spentTime: String by SharedStatsBinder.bind(this, spentTime)
    override var progressBar: String by SharedStatsBinder.bind(this, progressBar)
}

class BaseInsertUnitStats(
        origin: String,
        fileName: String,
        fileSize: Long,
        status: InsertUnitStatus
) : InsertUnitStats, SharedStats(origin) {
    override val fileName: String by SharedStatsBinder.bind(this, fileName)
    override val fileSize: Long by SharedStatsBinder.bind(this, fileSize)
    override val status: InsertUnitStatus by SharedStatsBinder.bind(this, status)

}
