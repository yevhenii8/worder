package worder.database

class DbSummary {
    private val map = mutableMapOf(
        "total" to 0,
        "toLearn" to 0,
        "learned" to 0,
        "updated" to 0
    )

    var total by map
    var toLearn by map
    var learned by map
    var updated by map

    fun asMap(): Map<String, Int> = map.toMap()
}