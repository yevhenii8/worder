/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <MacmillanRequester.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <24/07/2020, 07:45:55 PM>
 * Version: <10>
 */

package worder.update.model.impl.requesters

import worder.core.model.BareWord
import worder.update.model.DefinitionRequester
import worder.update.model.ExampleRequester
import worder.update.model.Requester
import worder.update.model.TranscriptionRequester
import worder.update.model.impl.WebsiteRequesterDecorator
import worder.update.model.impl.WebsiteRequesterDecorator.Companion.sendGetRequest

class MacmillanRequester private constructor() : DefinitionRequester, ExampleRequester, TranscriptionRequester {
    companion object {
        private const val SITE_URL = "https://www.macmillandictionary.com/dictionary/british/"

        private val TRANSCRIPTION_PATTERN = Regex("(?<=<span class=\"SEP PRON-before\"> /</span>).*?(?=<)")
        private val DEFINITION_PATTERN = Regex("(?<=<span class=\"DEFINITION\">).*?(?=</span><div)")
        private val EXAMPLE_PATTERN = Regex("(?<=<p class=\"EXAMPLE\").*?(?=</p>)")
        private val COMMON_FILTER = Regex("(<.*?>)|(resource=\"dict_british\">)")

        val instance: Requester by lazy {
            object : WebsiteRequesterDecorator(MacmillanRequester()), DefinitionRequester, ExampleRequester, TranscriptionRequester {}
        }
    }


    override lateinit var definitions: List<String>
        private set
    override lateinit var examples: List<String>
        private set
    override lateinit var transcriptions: List<String>
        private set


    override suspend fun requestWord(word: BareWord) {
        val body = sendGetRequest(SITE_URL + word.name) + sendGetRequest("$SITE_URL${word.name}_1")

        definitions = DEFINITION_PATTERN.findAll(body)
                .map { COMMON_FILTER.replace(it.value, "").trim() }
                .toList()

        examples = EXAMPLE_PATTERN.findAll(body)
                .map { COMMON_FILTER.replace(it.value, "").trim() }
                .toList()

        transcriptions = TRANSCRIPTION_PATTERN.findAll(body)
                .map { "[${it.value}]" }
                .toList()
    }
}
