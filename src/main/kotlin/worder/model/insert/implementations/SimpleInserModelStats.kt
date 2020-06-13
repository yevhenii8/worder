package worder.model.insert.implementations

import worder.model.SimpleObservableStats
import worder.model.insert.InsertModelStats

open class SimpleInsertModelStats(
        origin: String = "Insert Model Stats",

        generatedUnits: Int = 0,
        uncommittedUnits: Int = 0,
        committedUnits: Int = 0,
        excludedUnits: Int = 0,
        actionNeededUnits: Int = 0,

        totalValidWords: Int = 0,
        totalInvalidWords: Int = 0,

        reset: Int = 0,
        inserted: Int = 0
) : SimpleObservableStats(origin), InsertModelStats {
    override var generatedUnits: Int by bindToStats(initValue = generatedUnits, defaultTitle = "Generated units")
    override var uncommittedUnits: Int by bindToStats(initValue = uncommittedUnits, defaultTitle = "Uncommitted units")
    override var committedUnits: Int by bindToStats(initValue = committedUnits, defaultTitle = "Committed units")
    override var excludedUnits: Int by bindToStats(initValue = excludedUnits, defaultTitle = "Excluded units")
    override var actionNeededUnits: Int by bindToStats(initValue = actionNeededUnits, defaultTitle = "Action needed untis")

    override var totalValidWords: Int by bindToStats(initValue = totalValidWords, defaultTitle = "Total valid words")
    override var totalInvalidWords: Int by bindToStats(initValue = totalInvalidWords, defaultTitle = "Total invalid words")

    override var reset: Int by bindToStats(initValue = reset, defaultTitle = "Reset words")
    override var inserted: Int by bindToStats(initValue = inserted, defaultTitle = "Inserted words")
}
