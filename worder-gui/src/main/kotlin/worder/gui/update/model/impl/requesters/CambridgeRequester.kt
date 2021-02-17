/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <CambridgeRequester.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <17/02/2021, 05:14:42 PM>
 * Version: <24>
 */

package worder.gui.update.model.impl.requesters

import worder.gui.core.model.BareWord
import worder.gui.update.model.DefinitionRequester
import worder.gui.update.model.ExampleRequester
import worder.gui.update.model.Requester
import worder.gui.update.model.impl.WebsiteRequesterDecorator
import worder.gui.update.model.impl.WebsiteRequesterDecorator.Companion.sendGetRequest

class CambridgeRequester private constructor() : DefinitionRequester, ExampleRequester {
    companion object {
        private const val SITE_URL = "https://dictionary.cambridge.org/search/direct/?datasetsearch=english&q="

        private val DEFINITION_PATTERN = Regex("(?<=<div class=\"def ddef_d db\">).*?(?=</div>)")
        private val EXAMPLE_PATTERN = Regex("(?<=<span class=\"eg deg\">).*?(?=</span></div>)")
        private val COMMON_FILTER = Regex("(<span class=\"lab dlab\">.*</span>)|(<.*?>)|(: )")

        val instance: Requester by lazy {
            object : WebsiteRequesterDecorator(CambridgeRequester()), DefinitionRequester, ExampleRequester {}
        }
    }


    override lateinit var definitions: List<String>
        private set

    override lateinit var examples: List<String>
        private set


    override suspend fun requestWord(word: BareWord) {
        val body = sendGetRequest(SITE_URL + word.name.replace(' ', '+'))

        definitions = DEFINITION_PATTERN.findAll(body)
                .map { COMMON_FILTER.replace(it.value, "").trim() }
                .toList()

        examples = EXAMPLE_PATTERN.findAll(body)
                .map { COMMON_FILTER.replace(it.value, "").trim() }
                .toList()
    }
}
