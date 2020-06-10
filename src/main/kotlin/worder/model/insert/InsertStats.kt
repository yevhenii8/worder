package worder.model.insert

import worder.model.Stats
import worder.model.insert.InsertModel.InsertModelStatus

interface InsertModelStats : Stats {
    val status: InsertModelStatus

    val generatedUnits: Int
    val committedUnits: Int

    val totalValidWords: Int
    val totalInvalidWords: Int

    val reset: Int
    val inserted: Int
}
