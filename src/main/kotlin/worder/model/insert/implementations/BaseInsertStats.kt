package worder.model.insert.implementations

import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.SimpleMapProperty
import worder.model.SharedStats
import worder.model.insert.InsertModelStats

//class BaseInsertModelStats(
//        origin: String = "Insert Model Stats",
//
//        generatedUnits: Int = 0,
//        committedUnits: Int = 0,
//
//        totalValidWords: Int = 0,
//        totalInvalidWords: Int = 0,
//
//        reset: Int = 0,
//        inserted: Int = 0, override val uncommittedUnits: Int = 5, override val excludedUnits: Int = 5,
//        override val asMapProperty: ReadOnlyMapProperty<String, Any?> = SimpleMapProperty()
//) : InsertModelStats, SharedStats(origin) {
//    override var generatedUnits: Int by SharedStatsBinder.bind(this, generatedUnits)
//    override var committedUnits: Int by SharedStatsBinder.bind(this, committedUnits)
//
//    override var totalValidWords: Int by SharedStatsBinder.bind(this, totalValidWords)
//    override var totalInvalidWords: Int by SharedStatsBinder.bind(this, totalInvalidWords)
//
//    override var reset: Int by SharedStatsBinder.bind(this, reset)
//    override var inserted: Int by SharedStatsBinder.bind(this, inserted)
//}
