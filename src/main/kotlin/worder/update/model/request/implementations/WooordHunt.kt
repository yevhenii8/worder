package worder.model.insert.request.implementations

import worder.core.model.Word
import worder.model.insert.request.*
import worder.model.insert.request.RequesterBaseDecorator.Companion.sendGetRequest


class WooordHunt private constructor() : TranslationRequester, TranscriptionRequester {
    companion object {
        private const val SITE_URL = "https://wooordhunt.ru/word/"

        private val TRANSCRIPTION_PATTERN = Regex("(?<=<span title=\"американская транскрипция слова )(.*?)\" class=\"transcription\"> \\|(.*?)(?=\\|<)")
        private val TRANSLATION_PATTERN = Regex("(?<=<span class=\"t_inline_en\">).*?(?=</span>)")

        val instance: Requester by lazy {
            object : RequesterBaseDecorator(WooordHunt()), TranslationRequester, TranscriptionRequester {}
        }
    }


    override lateinit var translations: Set<String>
        private set
    override lateinit var transcriptions: Set<String>
        private set


    override suspend fun requestWord(word: Word) {
        val body = word.sendGetRequest(SITE_URL + word.name)

        translations = TRANSLATION_PATTERN.find(body)?.value?.split(", ")?.map { it.trim().toLowerCase() }?.toSet() ?: emptySet()

        transcriptions = TRANSCRIPTION_PATTERN.findAll(body)
            .map { "[${it.groupValues[2]}]" }
            .toSet()
    }
}
