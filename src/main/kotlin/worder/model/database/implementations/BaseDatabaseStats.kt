package worder.model.database.implementations

import worder.model.SharedStats
import worder.model.database.InserterSessionStats
import worder.model.database.UpdaterSessionStats
import worder.model.database.WorderSummaryStats
import worder.model.database.WorderTrackStats

class BaseWorderTrackStats(
        origin: String = "Worder App Tracker",
        totalInserted: Int = 0,
        totalReset: Int = 0,
        totalUpdated: Int = 0
) : WorderTrackStats, SharedStats(origin) {
    override var totalAffected: Int by SharedStatsBinder.bind(this, totalInserted + totalReset + totalUpdated)
    override var totalInserted: Int by SharedStatsBinder.bind(this, totalInserted)
    override var totalReset: Int by SharedStatsBinder.bind(this, totalReset)
    override var totalUpdated: Int by SharedStatsBinder.bind(this, totalUpdated)
}

class BaseWorderSummaryStats(
        origin: String = "Database Summary",
        unlearned: Int = 0,
        learned: Int = 0
) : WorderSummaryStats, SharedStats(origin) {
    override var totalAmount: Int by SharedStatsBinder.bind(this, unlearned + learned)
    override var unlearned: Int by SharedStatsBinder.bind(this, unlearned)
    override var learned: Int by SharedStatsBinder.bind(this, learned)
}

class BaseUpdaterSessionStats(
        origin: String = "Updater Session",
        removed: Int = 0,
        updated: Int = 0,
        skipped: Int = 0,
        learned: Int = 0
) : UpdaterSessionStats, SharedStats(origin) {
    override var totalProcessed: Int by SharedStatsBinder.bind(this, removed + updated + skipped + learned)
    override var removed: Int by SharedStatsBinder.bind(this, removed)
    override var updated: Int by SharedStatsBinder.bind(this, updated)
    override var skipped: Int by SharedStatsBinder.bind(this, skipped)
    override var learned: Int by SharedStatsBinder.bind(this, learned)

}

class BaseInserterSessionStats(
        origin: String = "Inserter Session",
        inserted: Int = 0,
        reset: Int = 0
) : InserterSessionStats, SharedStats(origin) {
    override var totalProcessed: Int by SharedStatsBinder.bind(this, inserted + reset)
    override var inserted: Int by SharedStatsBinder.bind(this, inserted)
    override var reset: Int by SharedStatsBinder.bind(this, reset)
}
