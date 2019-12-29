package worder.request.sites

import worder.model.Word
import worder.request.*
import java.net.URL


class WooordHunt private constructor(): TranslationRequester, TranscriptionRequester {
    companion object : RequesterProducer {
        private const val SITE_URL = "https://wooordhunt.ru/word/"

        private val TRANSCRIPTION_PATTERN = Regex("(?<=<span title=\"американская транскрипция слова )(.*?)\" class=\"transcription\"> \\|(.*?)(?=\\|<)")
        private val TRANSLATION_PATTERN = Regex("(?<=<span class=\"t_inline_en\">).*?(?=</span>)")

        override fun newInstance(): Requester = object : RequesterStatDecorator(WooordHunt()), TranslationRequester, TranscriptionRequester { }
    }


    private var siteBody: String = ""

    override fun acceptWord(word: Word) {
        siteBody = if (!word.name.contains(" ")) URL(SITE_URL + word.name).readText() else ""
    }

    override fun getTranslations(): Set<String> {
        return TRANSLATION_PATTERN.find(siteBody)?.let { matchResult ->
            matchResult.value.split(", ")
                .map { it.trim().toLowerCase() }
                .toSet()
        } ?: emptySet()
    }

    override fun getTranscriptions(): Set<String> =
        TRANSCRIPTION_PATTERN.findAll(siteBody)
            .map { "[${it.groupValues[2]}]" }
            .toSet()
}
