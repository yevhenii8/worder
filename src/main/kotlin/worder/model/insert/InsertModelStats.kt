package worder.model.insert

import worder.model.ObservableStats

interface InsertModelStats : ObservableStats {
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
