package worder.model.database

import worder.model.Stats

interface WorderSummaryStats : Stats {
    val totalAmount: Int
    val unlearned: Int
    val learned: Int
}

interface WorderTrackStats : Stats {
    val totalInserted: Int
    val totalReset: Int
    val totalUpdated: Int
    val totalAffected: Int
}

interface UpdaterSessionStats : Stats {
    val totalProcessed: Int
    val removed: Int
    val updated: Int
    val skipped: Int
    val learned: Int
}

interface InserterSessionStats : Stats {
    val totalProcessed: Int
    val inserted: Int
    val reset: Int
}
