package worder.insert.model.implementations

import javafx.beans.property.SimpleIntegerProperty
import worder.core.model.BaseObservableStats
import worder.insert.model.ObservableInsertModelStats

open class SimpleInsertModelStats(
        origin: String = "Insert Model Stats",

        generatedUnits: Int = 0,
        uncommittedUnits: Int = 0,
        committedUnits: Int = 0,
        excludedUnits: Int = 0,
        actionNeededUnits: Int = 0,

        totalValidWords: Int = 0,
        totalInvalidWords: Int = 0,

        totalProcessed: Int = 0,
        reset: Int = 0,
        inserted: Int = 0
) : BaseObservableStats(origin), ObservableInsertModelStats {
    final override val generatedUnitsProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Generated units", generatedUnits)
    override var generatedUnits: Int by bindToStats(generatedUnitsProperty)

    final override var uncommittedUnitsProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Uncommitted units", uncommittedUnits)
    override var uncommittedUnits: Int by bindToStats(uncommittedUnitsProperty)

    final override var committedUnitsProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Committed units", committedUnits)
    override var committedUnits: Int by bindToStats(committedUnitsProperty)

    final override var excludedUnitsProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Excluded units", excludedUnits)
    override var excludedUnits: Int by bindToStats(excludedUnitsProperty)

    final override var actionNeededUnitsProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Action needed units", actionNeededUnits)
    override var actionNeededUnits: Int by bindToStats(actionNeededUnitsProperty)


    final override var totalValidWordsProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Total valid words", totalValidWords)
    override var totalValidWords: Int by bindToStats(totalValidWordsProperty)

    final override var totalInvalidWordsProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Total invalid words", totalInvalidWords)
    override var totalInvalidWords: Int by bindToStats(totalInvalidWordsProperty)


    final override var totalProcessedProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Total processed words", totalProcessed)
    override var totalProcessed: Int by bindToStats(totalProcessedProperty)

    final override var insertedProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Inserted words", inserted)
    override var inserted: Int by bindToStats(insertedProperty)

    final override var resetProperty: SimpleIntegerProperty = SimpleIntegerProperty(null, "Reset words", reset)
    override var reset: Int by bindToStats(resetProperty)
}
