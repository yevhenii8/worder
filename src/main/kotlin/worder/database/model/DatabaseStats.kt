package worder.database.model

import worder.core.model.ObservableStats

interface ObservableWorderSummaryStats : ObservableStats {
    val totalAmount: Int
    val unlearned: Int
    val learned: Int
}

interface ObservableWorderTrackStats : ObservableStats {
    val totalInserted: Int
    val totalReset: Int
    val totalUpdated: Int
    val totalAffected: Int
}

interface ObservableUpdaterStats : ObservableStats {
    val totalProcessed: Int
    val removed: Int
    val updated: Int
    val skipped: Int
    val learned: Int
}

interface ObservableInserterStats : ObservableStats {
    val totalProcessed: Int
    val inserted: Int
    val reset: Int
}
