package worder.model.insert.implementations

import worder.model.SharedStats
import worder.model.insert.InsertBatch.InsertBatchStatus
import worder.model.insert.InsertBatchStats
import worder.model.insert.InsertUnit.InsertUnitStatus
import worder.model.insert.InsertUnitStats

class BaseInsertBatchStats(
        origin: String = "Insert Batch Stats",
        id: String,
        status: InsertBatchStatus? = null,

        committedUnits: Int = 0,
        totalProcessed: Int = 0,
        validProcessed: Int = 0,
        invalidProcessed: Int = 0,
        reset: Int = 0,
        inserted: Int = 0
) : InsertBatchStats, SharedStats(origin) {
    override val id: String by SharedStatsBinder.bind(this, id)
    override var status: InsertBatchStatus by SharedStatsBinder.bind(this, status)

    override var committedUnits: Int by SharedStatsBinder.bind(this, committedUnits)
    override var totalProcessed: Int by SharedStatsBinder.bind(this, totalProcessed)
    override var validProcessed: Int by SharedStatsBinder.bind(this, validProcessed)
    override var invalidProcessed: Int by SharedStatsBinder.bind(this, invalidProcessed)
    override var reset: Int by SharedStatsBinder.bind(this, reset)
    override var inserted: Int by SharedStatsBinder.bind(this, inserted)
}

class BaseInsertUnitStats(
        origin: String = "Inserter Unit Stats",
        id: String,
        status: InsertUnitStatus? = null,

        fileName: String,
        fileSize: Long,

        invalidWords: Int,
        validWords: Int
) : InsertUnitStats, SharedStats(origin) {
    override val id: String by SharedStatsBinder.bind(this, id)
    override var status: InsertUnitStatus by SharedStatsBinder.bind(this, status)

    override val fileName: String by SharedStatsBinder.bind(this, fileName)
    override val fileSize: Long by SharedStatsBinder.bind(this, fileSize)

    override val invalidWords: Int by SharedStatsBinder.bind(this, invalidWords)
    override val validWords: Int by SharedStatsBinder.bind(this, validWords)
}
