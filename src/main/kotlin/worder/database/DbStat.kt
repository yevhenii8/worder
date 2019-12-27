package worder.database

sealed class DbStat : Iterable<Map.Entry<String, Int>> {
    protected abstract val map: Map<String, Int>

    override fun iterator(): Iterator<Map.Entry<String, Int>> = map.iterator()
    override fun toString(): String = map.toString()
}


class DbSummary(total: Int, unlearned: Int, learned: Int) : DbStat() {
    override val map = mapOf(
        "total" to total,
        "learned" to learned,
        "unlearned" to unlearned
    )

    val total by map
    val learned by map
    val unlearned by map
}

class DbWorderTrack(totalInserted: Int, totalReset: Int, totalUpdated: Int) : DbStat() {
    override val map = mapOf(
        "totalInserted" to totalInserted,
        "totalReset" to totalReset,
        "totalUpdated" to totalUpdated
    )

    val totalInserted by map
    val totalReset by map
    val totalUpdated by map
}


class UpdaterSessionStat(removed: Int, updated: Int, skipped: Int, learned: Int) : DbStat() {
    override val map = mapOf(
        "removed" to removed,
        "updated" to updated,
        "skipped" to skipped,
        "learned" to learned
    )

    val removed by map
    val updated by map
    val skipped by map
    val learned by map
}

class InserterSessionStat(inserted: Int, reset: Int) : DbStat() {
    override val map = mapOf(
        "inserted" to inserted,
        "reset" to reset
    )

    val inserted by map
    val reset by map
}
