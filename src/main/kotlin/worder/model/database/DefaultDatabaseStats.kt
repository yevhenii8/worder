//package worder.model.database
//
//import worder.model.AbstractStats
//
//class DefaultSummaryStat(
//        override val origin: String,
//        total: Int,
//        unlearned: Int,
//        learned: Int
//) : AbstractStats(), DatabaseSummaryStats {
//    override var total: Int by map
//    override var unlearned: Int by map
//    override var learned: Int by map
//
//    init {
//        map["origin"] = origin
//        map["total"] = total
//        map["unlearned"] = unlearned
//        map["learned"] = learned
//    }
//}
//
//class DefaultWorderTrack(
//        override val origin: String,
//        totalInserted: Int,
//        totalReset: Int,
//        totalUpdated: Int
//) : AbstractStats(), WorderTrackStats {
//    override var totalInserted: Int by map
//    override var totalReset: Int by map
//    override var totalUpdated: Int by map
//
//    init {
//        map["totalInserted"] = totalInserted
//        map["totalReset"] = totalReset
//        map["totalUpdated"] = totalUpdated
//    }
//}
