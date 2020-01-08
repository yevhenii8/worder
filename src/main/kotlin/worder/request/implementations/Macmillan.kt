package worder.request.implementations

import worder.Word
import worder.request.*


class Macmillan private constructor() : DefinitionRequester, ExampleRequester, TranscriptionRequester {
    companion object : RequesterProducer {
        private const val SITE_URL = "https://www.macmillandictionary.com/dictionary/british/"

        private val TRANSCRIPTION_PATTERN = Regex("(?<=<span class=\"SEP PRON-before\"> /</span>).*?(?=<)")
        private val DEFINITION_PATTERN = Regex("(?<=<span class=\"DEFINITION\">).*?(?=</span><div)")
        private val EXAMPLE_PATTERN = Regex("(?<=<p class=\"EXAMPLE\").*?(?=</p>)")
        private val COMMON_FILTER = Regex("(<.*?>)|(resource=\"dict_british\">)")

        override fun newInstance(): Requester =
            object : RequesterStatDecorator(Macmillan()), DefinitionRequester, ExampleRequester, TranscriptionRequester {}
    }


    override lateinit var definitions: Set<String>
        private set
    override lateinit var examples: Set<String>
        private set
    override lateinit var transcriptions: Set<String>
        private set


    override suspend fun requestWord(word: Word) {
        val body = word.sendAsyncRequest(SITE_URL + word.name) + word.sendAsyncRequest("$SITE_URL${word.name}_1")

        definitions = DEFINITION_PATTERN.findAll(body)
            .map { COMMON_FILTER.replace(it.value, "").trim() }
            .toSet()

        examples = EXAMPLE_PATTERN.findAll(body)
            .map { COMMON_FILTER.replace(it.value, "").trim() }
            .toSet()

        transcriptions = TRANSCRIPTION_PATTERN.findAll(body)
            .map { "[${it.value}]" }
            .toSet()
    }
}
