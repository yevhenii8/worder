package worder.insert.model

import worder.core.model.ObservableStats

interface ObservableInsertModelStats : ObservableStats {
    val generatedUnits: Int
    val uncommittedUnits: Int
    val committedUnits: Int
    val excludedUnits: Int
    val actionNeededUnits: Int

    val totalValidWords: Int
    val totalInvalidWords: Int

    val reset: Int
    val inserted: Int
}
