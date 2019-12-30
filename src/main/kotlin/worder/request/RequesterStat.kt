package worder.request


class RequesterStat(
    val className: String,
    definitions: Int?,
    translations: Int?,
    examples: Int?,
    transcriptions: Int?
    ) : Iterable<Map.Entry<String, Int>>
{
    private val map: Map<String, Int> = mapOf(
        "definitions" to definitions,
        "translations" to translations,
        "examples" to examples,
        "transcriptions" to transcriptions
        ).filterValues { it != null } as Map<String, Int>


    override fun iterator(): Iterator<Map.Entry<String, Int>> = map.iterator()
    override fun toString(): String = "${className}: $map"
}
