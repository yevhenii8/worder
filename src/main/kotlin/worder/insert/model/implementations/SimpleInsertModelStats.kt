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
    final override val generatedUnitsObservable: IntegerProperty = SimpleIntegerProperty(null, "Generated units", generatedUnits)
    override var generatedUnits: Int by bindToPropertyAndStats(generatedUnitsObservable)

    final override val uncommittedUnitsObservable: IntegerProperty = SimpleIntegerProperty(null, "Uncommitted units", uncommittedUnits)
    override var uncommittedUnits: Int by bindToPropertyAndStats(uncommittedUnitsObservable)

    final override val committedUnitsObservable: IntegerProperty = SimpleIntegerProperty(null, "Committed units", committedUnits)
    override var committedUnits: Int by bindToPropertyAndStats(committedUnitsObservable)

    final override val excludedUnitsObservable: IntegerProperty = SimpleIntegerProperty(null, "Excluded units", excludedUnits)
    override var excludedUnits: Int by bindToPropertyAndStats(excludedUnitsObservable)

    final override val actionNeededUnitsObservable: IntegerProperty = SimpleIntegerProperty(null, "Action needed units", actionNeededUnits)
    override var actionNeededUnits: Int by bindToPropertyAndStats(actionNeededUnitsObservable)


    final override val totalValidWordsObservable: IntegerProperty = SimpleIntegerProperty(null, "Total valid words", totalValidWords)
    override var totalValidWords: Int by bindToPropertyAndStats(totalValidWordsObservable)

    final override val totalInvalidWordsObservable: IntegerProperty = SimpleIntegerProperty(null, "Total invalid words", totalInvalidWords)
    override var totalInvalidWords: Int by bindToPropertyAndStats(totalInvalidWordsObservable)


    final override val totalProcessedObservable: IntegerProperty = SimpleIntegerProperty(null, "Total processed words", totalProcessed)
    override var totalProcessed: Int by bindToPropertyAndStats(totalProcessedObservable)

    final override val insertedObservable: IntegerProperty = SimpleIntegerProperty(null, "Inserted words", inserted)
    override var inserted: Int by bindToPropertyAndStats(insertedObservable)

    final override val resetObservable: IntegerProperty = SimpleIntegerProperty(null, "Reset words", reset)
    override var reset: Int by bindToPropertyAndStats(resetObservable)
}