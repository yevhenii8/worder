package worder.model.database

import worder.model.ObservableStats

interface WorderSummaryStats : ObservableStats {
    val totalAmount: Int
    val unlearned: Int
    val learned: Int
}

interface WorderTrackStats : ObservableStats {
    val totalInserted: Int
    val totalReset: Int
    val totalUpdated: Int
    val totalAffected: Int
}

interface UpdaterStats : ObservableStats {
    val totalProcessed: Int
    val removed: Int
    val updated: Int
    val skipped: Int
    val learned: Int
}

interface InserterStats : ObservableStats {
    val totalProcessed: Int
    val inserted: Int
    val reset: Int
}
