/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <SimpleInsertBatchStats.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <01/08/2020, 09:44:12 PM>
 * Version: <13>
 */

package worder.insert.model.impl

import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import tornadofx.getValue
import tornadofx.setValue
import worder.core.model.BaseObservableStats
import worder.insert.model.ObservableInsertBatchStats

class SimpleInsertBatchStats(
        origin: String = "Insert Model Stats",

        generatedUnits: Int = 0,
        readyToCommitUnits: Int = 0,
        committedUnits: Int = 0,
        excludedUnits: Int = 0,
        actionNeededUnits: Int = 0,

        totalWords: Int = 0,
        totalValidWords: Int = 0,
        totalInvalidWords: Int = 0,

        totalProcessed: Int = 0,
        reset: Int = 0,
        inserted: Int = 0
) : BaseObservableStats(origin), ObservableInsertBatchStats {
    override val generatedUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Generated units", generatedUnits)
    override var generatedUnits: Int by bindThroughIntegerProperty(generatedUnitsProperty)

    override val readyToCommitUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Ready to commit", readyToCommitUnits)
    override var readyToCommitUnits: Int by bindThroughIntegerProperty(readyToCommitUnitsProperty)

    override val committedUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Committed", committedUnits)
    override var committedUnits: Int by bindThroughIntegerProperty(committedUnitsProperty)

    override val excludedUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Excluded", excludedUnits)
    override var excludedUnits: Int by bindThroughIntegerProperty(excludedUnitsProperty)

    override val actionNeededUnitsProperty: IntegerProperty = SimpleIntegerProperty(null, "Action needed", actionNeededUnits)
    override var actionNeededUnits: Int by bindThroughIntegerProperty(actionNeededUnitsProperty)


    override val totalWordsProperty: IntegerProperty = SimpleIntegerProperty(null, "Total uploaded", totalWords)
    override var totalWords: Int by bindThroughIntegerProperty(totalWordsProperty)

    override val totalValidWordsProperty: IntegerProperty = SimpleIntegerProperty(null, "Total valid words", totalValidWords)
    override var totalValidWords: Int by bindThroughIntegerProperty(totalValidWordsProperty)

    override val totalInvalidWordsProperty: IntegerProperty = SimpleIntegerProperty(null, "Total invalid words", totalInvalidWords)
    override var totalInvalidWords: Int by bindThroughIntegerProperty(totalInvalidWordsProperty)


    override val totalProcessedProperty: IntegerProperty = SimpleIntegerProperty(null, "Total processed", totalProcessed)
    override var totalProcessed: Int by bindThroughIntegerProperty(totalProcessedProperty)

    override val insertedProperty: IntegerProperty = SimpleIntegerProperty(null, "Inserted words", inserted)
    override var inserted: Int by bindThroughIntegerProperty(insertedProperty)

    override val resetProperty: IntegerProperty = SimpleIntegerProperty(null, "Reset words", reset)
    override var reset: Int by bindThroughIntegerProperty(resetProperty)
}
