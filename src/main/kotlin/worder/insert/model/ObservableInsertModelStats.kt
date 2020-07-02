package worder.insert.model

import javafx.beans.value.ObservableIntegerValue
import worder.core.model.ObservableStats

interface ObservableInsertModelStats : ObservableStats {
    /**
     * Contains stats values related to units
     */

    val generatedUnitsObservable: ObservableIntegerValue
    val generatedUnits: Int

    val uncommittedUnitsObservable: ObservableIntegerValue
    val uncommittedUnits: Int

    val committedUnitsObservable: ObservableIntegerValue
    val committedUnits: Int

    val excludedUnitsObservable: ObservableIntegerValue
    val excludedUnits: Int

    val actionNeededUnitsObservable: ObservableIntegerValue
    val actionNeededUnits: Int


    /**
     * Contains stats values related to processed files
     */

    val totalValidWordsObservable: ObservableIntegerValue
    val totalValidWords: Int

    val totalInvalidWordsObservable: ObservableIntegerValue
    val totalInvalidWords: Int


    /**
     * Contains stats values related to InsertModel itself
     */

    val totalProcessedObservable: ObservableIntegerValue
    val totalProcessed: Int

    val insertedObservable: ObservableIntegerValue
    val inserted: Int

    val resetObservable: ObservableIntegerValue
    val reset: Int
}
