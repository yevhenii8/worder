package worder.request.implementations

import worder.Word
import worder.request.*


class WooordHunt private constructor() : TranslationRequester, TranscriptionRequester {
    companion object : RequesterProducer {
        private const val SITE_URL = "https://wooordhunt.ru/word/"

        private val TRANSCRIPTION_PATTERN = Regex("(?<=<span title=\"американская транскрипция слова )(.*?)\" class=\"transcription\"> \\|(.*?)(?=\\|<)")
        private val TRANSLATION_PATTERN = Regex("(?<=<span class=\"t_inline_en\">).*?(?=</span>)")

        override fun newInstance(): Requester = object : RequesterStatDecorator(WooordHunt()), TranslationRequester, TranscriptionRequester {}
    }


    override lateinit var translations: Set<String>
        private set
    override lateinit var transcriptions: Set<String>
        private set


    override suspend fun requestWord(word: Word) {
        val body = word.sendAsyncRequest(SITE_URL + word.name)

        translations = TRANSLATION_PATTERN.find(body)?.value?.split(", ")?.map { it.trim().toLowerCase() }?.toSet() ?: emptySet()

        transcriptions = TRANSCRIPTION_PATTERN.findAll(body)
            .map { "[${it.groupValues[2]}]" }
            .toSet()
    }
}
