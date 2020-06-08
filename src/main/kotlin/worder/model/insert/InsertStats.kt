package worder.model.insert

import worder.model.Stats
import worder.model.insert.InsertModel.InsertModelStatus
import worder.model.insert.InsertUnit.InsertUnitStatus

interface InsertModelStats : Stats {
    val status: InsertModelStatus

    val generatedUnits: Int
    val committedUnits: Int

    val totalValidWords: Int
    val totalInvalidWords: Int

    val reset: Int
    val inserted: Int
}

interface InsertUnitStats : Stats {
    val id: String
    val status: InsertUnitStatus
    val source: String

    val invalidWords: Int
    val validWords: Int
}
