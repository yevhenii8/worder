package worder.request

import worder.AbstractStat


class RequesterStat(override val origin: String, definitions: Int?, translations: Int?, examples: Int?, transcriptions: Int?) : AbstractStat() {
    override val map: Map<String, String> = mapOf(
        "definitions" to definitions.toString(),
        "translations" to translations.toString(),
        "examples" to examples.toString(),
        "transcriptions" to transcriptions.toString()
    ).filterValues { it != "null" }
}
