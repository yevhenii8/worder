package worder.model.database

import worder.model.Stats

interface DatabaseSummaryStats : Stats {
    val total: Int
    val unlearned: Int
    val learned: Int
}

interface WorderTrackStats : Stats {
    val totalInserted: Int
    val totalReset: Int
    val totalUpdated: Int
}
