package worder.model.insert.implementations

import worder.model.SharedStats
import worder.model.insert.InsertModel.InsertModelStatus
import worder.model.insert.InsertModelStats
import worder.model.insert.InsertUnit.InsertUnitStatus
import worder.model.insert.InsertUnitStats

class BaseInsertModelStats(
        origin: String = "Insert Model Stats",
        status: InsertModelStatus? = null,

        generatedUnits: Int = 0,
        committedUnits: Int = 0,

        totalValidWords: Int = 0,
        totalInvalidWords: Int = 0,

        reset: Int = 0,
        inserted: Int = 0
) : InsertModelStats, SharedStats(origin) {
    override val status: InsertModelStatus by SharedStatsBinder.bind(this, status)

    override var generatedUnits: Int by SharedStatsBinder.bind(this, generatedUnits)
    override var committedUnits: Int by SharedStatsBinder.bind(this, committedUnits)

    override var totalValidWords: Int by SharedStatsBinder.bind(this, totalValidWords)
    override var totalInvalidWords: Int by SharedStatsBinder.bind(this, totalInvalidWords)

    override var reset: Int by SharedStatsBinder.bind(this, reset)
    override var inserted: Int by SharedStatsBinder.bind(this, inserted)
}

class BaseInsertUnitStats(
        origin: String = "Inserter Unit Stats",
        id: String,
        status: InsertUnitStatus? = null,
        source: String,

        invalidWords: Int,
        validWords: Int
) : InsertUnitStats, SharedStats(origin) {
    override val id: String by SharedStatsBinder.bind(this, id)
    override val status: InsertUnitStatus by SharedStatsBinder.bind(this, status)
    override val source: String by SharedStatsBinder.bind(this, source)

    override val invalidWords: Int by SharedStatsBinder.bind(this, invalidWords)
    override val validWords: Int by SharedStatsBinder.bind(this, validWords)
}
