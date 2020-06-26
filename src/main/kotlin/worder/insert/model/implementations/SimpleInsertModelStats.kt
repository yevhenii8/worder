package worder.insert.model.implementations

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.getValue
import tornadofx.setValue
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
    final override val generatedUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Generated units", generatedUnits)
    override var generatedUnits: Int by bindToPropertyAndStats(generatedUnitsProperty)

    final override val uncommittedUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Uncommitted units", uncommittedUnits)
    override var uncommittedUnits: Int by bindToPropertyAndStats(uncommittedUnitsProperty)

    final override val committedUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Committed units", committedUnits)
    override var committedUnits: Int by bindToPropertyAndStats(committedUnitsProperty)

    final override val excludedUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Excluded units", excludedUnits)
    override var excludedUnits: Int by bindToPropertyAndStats(excludedUnitsProperty)

    final override val actionNeededUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Action needed units", actionNeededUnits)
    override var actionNeededUnits: Int by bindToPropertyAndStats(actionNeededUnitsProperty)


    final override val totalValidWordsProperty: IntegerProperty = SimpleIntegerProperty(null, "Total valid words", totalValidWords)
    override var totalValidWords: Int by bindToPropertyAndStats(totalValidWordsProperty)

    final override val totalInvalidWordsProperty: IntegerProperty = SimpleIntegerProperty(null, "Total invalid words", totalInvalidWords)
    override var totalInvalidWords: Int by bindToPropertyAndStats(totalInvalidWordsProperty)


    final override val totalProcessedProperty: IntegerProperty = SimpleIntegerProperty(null, "Total processed words", totalProcessed)
    override var totalProcessed: Int by bindToPropertyAndStats(totalProcessedProperty)

    final override val insertedProperty: IntegerProperty = SimpleIntegerProperty(null, "Inserted words", inserted)
    override var inserted: Int by bindToPropertyAndStats(insertedProperty)

    final override val resetProperty: IntegerProperty = SimpleIntegerProperty(null, "Reset words", reset)
    override var reset: Int by bindToPropertyAndStats(resetProperty)
}
