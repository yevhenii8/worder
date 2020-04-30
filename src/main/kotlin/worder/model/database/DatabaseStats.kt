package worder.model.database

import worder.AbstractStat

sealed class DbStat : AbstractStat()

class DbSummary(override val origin: String, total: Int, unlearned: Int, learned: Int) : DbStat() {
    override val map: Map<String, String> = mapOf(
            "total" to total.toString(),
            "learned" to learned.toString(),
            "unlearned" to unlearned.toString()
    )
}

class DbWorderTrack(override val origin: String, totalInserted: Int, totalReset: Int, totalUpdated: Int) : DbStat() {
    override val map = mapOf(
            "totalInserted" to totalInserted.toString(),
            "totalReset" to totalReset.toString(),
            "totalUpdated" to totalUpdated.toString()
    )
}

class UpdaterSessionStat(override val origin: String, removed: Int, updated: Int, skipped: Int, learned: Int) : DbStat() {
    override val map = mapOf(
            "removed" to removed.toString(),
            "updated" to updated.toString(),
            "skipped" to skipped.toString(),
            "learned" to learned.toString()
    )
}

class InserterSessionStat(override val origin: String, inserted: Int, reset: Int) : DbStat() {
    override val map = mapOf(
            "inserted" to inserted.toString(),
            "reset" to reset.toString()
    )
}
