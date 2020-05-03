package worder.model.database.implementations

import worder.model.AbstractStats
import worder.model.database.DatabaseSummaryStats
import worder.model.database.WorderTrackStats

class SummaryStat(
        override val origin: String,
        override var total: Int,
        override var unlearned: Int,
        override var learned: Int
) : AbstractStats<DatabaseSummaryStats>(), DatabaseSummaryStats

class WorderTrack(
        override val origin: String,
        override var totalInserted: Int,
        override var totalReset: Int,
        override var totalUpdated: Int
) : AbstractStats<WorderTrackStats>(), WorderTrackStats
