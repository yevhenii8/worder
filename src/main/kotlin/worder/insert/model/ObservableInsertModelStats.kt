package worder.insert.model

import javafx.beans.property.SimpleIntegerProperty
import worder.core.model.ObservableStats

interface ObservableInsertModelStats : ObservableStats {
    /**
     * Contains stats values related to units
     */

    val generatedUnitsProperty: SimpleIntegerProperty
    val generatedUnits: Int

    val uncommittedUnitsProperty: SimpleIntegerProperty
    val uncommittedUnits: Int

    val committedUnitsProperty: SimpleIntegerProperty
    val committedUnits: Int

    val excludedUnitsProperty: SimpleIntegerProperty
    val excludedUnits: Int

    val actionNeededUnitsProperty: SimpleIntegerProperty
    val actionNeededUnits: Int


    /**
     * Contains stats values related to processed files
     */

    val totalValidWordsProperty: SimpleIntegerProperty
    val totalValidWords: Int

    val totalInvalidWordsProperty: SimpleIntegerProperty
    val totalInvalidWords: Int


    /**
     * Contains stats values related to InsertModel itself
     */
    val totalProcessedProperty: SimpleIntegerProperty
    val totalProcessed: Int

    val resetProperty: SimpleIntegerProperty
    val reset: Int

    val insertedProperty: SimpleIntegerProperty
    val inserted: Int
}
