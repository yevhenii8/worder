/*
package worder.model.extract.request

import worder.model.AbstractStat


sealed class RequesterStat : AbstractStat()


class RequesterSessionStat(
    override val origin: String,
    totalRequests: Int,
    totalDefinitions: Int?,
    totalTranslations: Int?,
    totalExamples: Int?,
    totalTranscriptions: Int?
) : RequesterStat() {
    override val map: Map<String, String> = mapOf(
        "totalRequests" to totalRequests.toString(),
        "totalDefinitions" to totalDefinitions.toString(),
        "totalTranslations" to totalTranslations.toString(),
        "totalExamples" to totalExamples.toString(),
        "totalTranscriptions" to totalTranscriptions.toString()
    ).filterValues { it != "null" }
}

class RequestStat(
    override val origin: String,
    word: String,
    definitions: Int?,
    translations: Int?,
    examples: Int?,
    transcriptions: Int?
) : RequesterStat() {
    override val map: Map<String, String> = mapOf(
        "word" to word,
        "definitions" to definitions.toString(),
        "translations" to translations.toString(),
        "examples" to examples.toString(),
        "transcriptions" to transcriptions.toString()
    ).filterValues { it != "null" }

}
*/
