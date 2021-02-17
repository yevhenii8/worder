/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WooordHuntRequester.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <17/02/2021, 05:24:49 PM>
 * Version: <15>
 */

package worder.gui.update.model.impl.requesters

import worder.gui.core.model.BareWord
import worder.gui.update.model.Requester
import worder.gui.update.model.TranscriptionRequester
import worder.gui.update.model.TranslationRequester
import worder.gui.update.model.impl.WebsiteRequesterDecorator
import worder.gui.update.model.impl.WebsiteRequesterDecorator.Companion.sendGetRequest

class WooordHuntRequester private constructor() : TranslationRequester, TranscriptionRequester {
    companion object {
        private const val SITE_URL = "https://wooordhunt.ru/word/"

        private val TRANSLATION_PATTERN = Regex("(?<=<div class=\"t_inline_en\">).*?(?=</div>)")
        private val TRANSCRIPTION_PATTERN = Regex(
            "(?<=<span title=\"американская транскрипция слова )(.*?)\" class=\"transcription\"> \\|(.*?)(?=\\|<)"
        )

        val instance: Requester by lazy {
            object : WebsiteRequesterDecorator(WooordHuntRequester()), TranslationRequester, TranscriptionRequester {}
        }
    }


    override lateinit var translations: List<String>
        private set
    override lateinit var transcriptions: List<String>
        private set


    override suspend fun requestWord(word: BareWord) {
        val body = sendGetRequest(SITE_URL + word.name.replace(" ", "%20"))

        translations = TRANSLATION_PATTERN.find(body)?.value
            ?.split(", ")
            ?.map { it.trim().toLowerCase() }
            ?.toList() ?: emptyList()

        transcriptions = TRANSCRIPTION_PATTERN.findAll(body)
            .map { it.groupValues[2] }
            .toList()
    }
}
