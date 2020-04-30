package worder


interface Stat : Iterable<Map.Entry<String, String>> {
    val origin: String

    fun asMap() : Map<String, String>
}


abstract class AbstractStat : Stat {
    protected abstract val map: Map<String, String>


    override fun iterator(): Iterator<Map.Entry<String, String>> = map.iterator()

    override fun toString(): String = "$origin: ${map.toString()}"

    override fun asMap(): Map<String, String> = map
}
