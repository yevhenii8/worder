package worder.request.implementations

import worder.Word
import worder.request.*
import worder.request.RequesterBaseDecorator.Companion.sendGetRequest


class Lingvo private constructor() : TranslationRequester, TranscriptionRequester {
    companion object {
        private const val SITE_URL = "https://www.lingvolive.com/en-us/translate/en-ru/"

        private val TRANSCRIPTION_PATTERN = Regex("(?<=<span class=\"_2EnCi Zf_4w _3bSyz IAnu-\")( data-reactid=\".*?\">)(.*?)(?=</span>)")
        private val TRANSLATION_PATTERN = Regex("(?<=<span class=\"_3zJig\").*?(?=</span>)")
        private val TRANSLATION_FILTER = Regex("(?U)[А-Яа-я ]+")
        private val TRANSLATION_BODY_FILTER = Regex("(?<=<div class=\"_1mexQ Zf_4w _3bSyz\").*?(?=</div><div class=\"(_3dLzG)|(#quote)\")")

        val instance: Requester by lazy {
            object : RequesterBaseDecorator(Lingvo()), TranslationRequester, TranscriptionRequester {}
        }
    }


    override lateinit var translations: Set<String>
        private set

    override lateinit var transcriptions: Set<String>
        private set


    override suspend fun requestWord(word: Word) {
        val body = word.sendGetRequest(SITE_URL + word.name)

        translations = TRANSLATION_BODY_FILTER.find(body)?.let { matchResult ->
            TRANSLATION_PATTERN.findAll(matchResult.value)
                .map { it.value.replace(".*>".toRegex(), "").trim() }
                .filter { str -> TRANSLATION_FILTER.matches(str) }
                .toSet()
        } ?: emptySet()

        transcriptions = TRANSCRIPTION_PATTERN.findAll(body)
            .map { it.groupValues[2].replace("&#x27;", "'") }
            .toSet()
    }
}
