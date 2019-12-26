package worder.database

sealed class DbStat : Iterable<Map.Entry<String, Int>> {
    protected abstract val map: Map<String, Int>

    override fun iterator(): Iterator<Map.Entry<String, Int>> = map.iterator()
    override fun toString(): String = map.toString()
}


class DbSessionStat(removed: Int = 0, updated: Int = 0, skipped: Int = 0) : DbStat() {
    override val map = mapOf(
        "removed" to removed,
        "updated" to updated,
        "skipped" to skipped
    )

    val removed by map
    val updated by map
    val skipped by map
}

class DbSummary(total: Int = 0, unlearned: Int = 0, learned: Int = 0) : DbStat() {
    override val map = mapOf(
        "total" to total,
        "learned" to learned,
        "unlearned" to unlearned
    )

    val total by map
    val learned by map
    val unlearned by map
}

class DbWorderTrack(totalInserted: Int = 0, totalReset: Int = 0, totalUpdated: Int = 0) : DbStat() {
    override val map = mapOf(
        "totalInserted" to totalInserted,
        "totalReset" to totalReset,
        "totalUpdated" to totalUpdated
    )

    val totalInserted by map
    val totalReset by map
    val totalUpdated by map
}
