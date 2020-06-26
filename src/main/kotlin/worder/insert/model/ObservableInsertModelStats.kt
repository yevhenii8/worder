package worder.insert.model

import javafx.beans.property.ReadOnlyIntegerProperty
import worder.core.model.ObservableStats

interface ObservableInsertModelStats : ObservableStats {
    /**
     * Contains stats values related to units
     */

    val generatedUnitsProperty: ReadOnlyIntegerProperty
    val generatedUnits: Int

    val uncommittedUnitsProperty: ReadOnlyIntegerProperty
    val uncommittedUnits: Int

    val committedUnitsProperty: ReadOnlyIntegerProperty
    val committedUnits: Int

    val excludedUnitsProperty: ReadOnlyIntegerProperty
    val excludedUnits: Int

    val actionNeededUnitsProperty: ReadOnlyIntegerProperty
    val actionNeededUnits: Int


    /**
     * Contains stats values related to processed files
     */

    val totalValidWordsProperty: ReadOnlyIntegerProperty
    val totalValidWords: Int

    val totalInvalidWordsProperty: ReadOnlyIntegerProperty
    val totalInvalidWords: Int


    /**
     * Contains stats values related to InsertModel itself
     */
    val totalProcessedProperty: ReadOnlyIntegerProperty
    val totalProcessed: Int

    val insertedProperty: ReadOnlyIntegerProperty
    val inserted: Int

    val resetProperty: ReadOnlyIntegerProperty
    val reset: Int
}
