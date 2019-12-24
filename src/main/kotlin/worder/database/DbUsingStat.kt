package worder.database

class DbUsingStat {
    private val map = mutableMapOf(
        "removed" to 0,
        "updated" to 0,
        "skipped" to 0
    )

    var removed by map
    var updated by map
    var skipped by map

    fun asMap(): Map<String, Int> = map.toMap()
}