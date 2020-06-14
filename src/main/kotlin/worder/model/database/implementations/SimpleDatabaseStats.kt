package worder.model.database.implementations

import worder.model.BaseObservableStats
import worder.model.database.InserterStats
import worder.model.database.UpdaterStats
import worder.model.database.WorderSummaryStats
import worder.model.database.WorderTrackStats

open class SimpleWorderTrackStats(
        origin: String = "Worder App Tracker",

        totalAffected: Int = 0,
        totalInserted: Int = 0,
        totalReset: Int = 0,
        totalUpdated: Int = 0
) : BaseObservableStats(origin), WorderTrackStats {
    override var totalAffected: Int by bindToStats(initValue = totalAffected, defaultTitle = "Total affected")
    override var totalInserted: Int by bindToStats(initValue = totalInserted, defaultTitle = "Total inserted")
    override var totalReset: Int by bindToStats(initValue = totalReset, defaultTitle = "Total reset")
    override var totalUpdated: Int by bindToStats(initValue = totalUpdated, defaultTitle = "Total updated")
}

open class SimpleWorderSummaryStats(
        origin: String = "Database Summary",

        totalAmount: Int = 0,
        unlearned: Int = 0,
        learned: Int = 0
) : BaseObservableStats(origin), WorderSummaryStats {
    override var totalAmount: Int by bindToStats(initValue = totalAmount, defaultTitle = "Total amount")
    override var unlearned: Int by bindToStats(initValue = unlearned, defaultTitle = "Unlearned")
    override var learned: Int by bindToStats(initValue = learned, defaultTitle = "Learned")
}

open class SimpleUpdaterStats(
        origin: String = "Updater Session",

        totalProcessed: Int = 0,
        removed: Int = 0,
        updated: Int = 0,
        skipped: Int = 0,
        learned: Int = 0
) : BaseObservableStats(origin), UpdaterStats {
    override var totalProcessed: Int by bindToStats(initValue = totalProcessed, defaultTitle = "Total processed")
    override var removed: Int by bindToStats(initValue = removed, defaultTitle = "Removed")
    override var updated: Int by bindToStats(initValue = updated, defaultTitle = "Updated")
    override var skipped: Int by bindToStats(initValue = skipped, defaultTitle = "Skipped")
    override var learned: Int by bindToStats(initValue = learned, defaultTitle = "Learned")

}

open class SimpleInserterStats(
        origin: String = "Inserter Session",

        totalProcessed: Int = 0,
        inserted: Int = 0,
        reset: Int = 0
) : BaseObservableStats(origin), InserterStats {
    override var totalProcessed: Int by bindToStats(initValue = totalProcessed, defaultTitle = "Total processed")
    override var inserted: Int by bindToStats(initValue = inserted, defaultTitle = "Inserted")
    override var reset: Int by bindToStats(initValue = reset, defaultTitle = "Reset")
}
