package worder.request.sites

import worder.model.Word
import worder.request.*
import java.net.URL


class Lingvo private constructor(): TranslationRequester, TranscriptionRequester {
    companion object : RequesterProducer {
        private const val SITE_URL = "https://www.lingvolive.com/en-us/translate/en-ru/"

        private val TRANSCRIPTION_PATTERN = Regex("(?<=<span class=\"_2EnCi Zf_4w _3bSyz IAnu-\")( data-reactid=\".*?\">)(.*?)(?=</span>)")
        private val TRANSLATION_PATTERN = Regex("(?<=<span class=\"_3zJig\").*?(?=</span>)")
        private val TRANSLATION_FILTER = Regex("(?U)[А-Яа-я ]+")
        private val TRANSLATION_BODY_FILTER = Regex("(?<=<div class=\"_1mexQ Zf_4w _3bSyz\").*?(?=</div><div class=\"(_3dLzG)|(#quote)\")")

        override fun newInstance(): Requester = object : RequesterStatDecorator(Lingvo()), TranslationRequester, TranscriptionRequester { }
    }


    private var siteBody: String = ""

    override fun acceptWord(word: Word) {
        siteBody = if (!word.name.contains(" ")) URL(SITE_URL + word.name).readText() else ""
    }

    override fun getTranslations(): Set<String> {
        return TRANSLATION_BODY_FILTER.find(siteBody)?.let { matchResult ->
            TRANSLATION_PATTERN.findAll(matchResult.value)
                .map { matchResult -> matchResult.value.replace(".*>".toRegex(), "").trim() }
                .filter { str -> TRANSLATION_FILTER.matches(str) }
                .toSet()
        } ?: emptySet()
    }

    override fun getTranscriptions(): Set<String> =
        TRANSCRIPTION_PATTERN.findAll(siteBody)
            .map { it.groupValues[2].replace("&#x27;", "'") }
            .toSet()
}
