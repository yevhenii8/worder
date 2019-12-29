package worder.request.sites

import worder.model.Word
import worder.request.*
import java.net.URL


class Macmillan private constructor() : DefinitionRequester, ExampleRequester, TranscriptionRequester {
    companion object : RequesterProducer {
        private const val SITE_URL = "https://www.macmillandictionary.com/dictionary/british/"

        private val TRANSCRIPTION_PATTERN = Regex("(?<=<span class=\"SEP PRON-before\"> /</span>).*?(?=<)")
        private val DEFINITION_PATTERN = Regex("(?<=<span class=\"DEFINITION\">).*?(?=</span><div)")
        private val EXAMPLE_PATTERN = Regex("(?<=<p class=\"EXAMPLE\").*?(?=</p>)")
        private val COMMON_FILTER = Regex("(<.*?>)|(resource=\"dict_british\">)")

        override fun newInstance(): Requester =
            object : RequesterStatDecorator(Macmillan()), DefinitionRequester, ExampleRequester, TranscriptionRequester { }
    }


    private var siteBody: String = ""

    override fun acceptWord(word: Word) {
        siteBody = if (!word.name.contains(" ")) URL("$SITE_URL${word.name}_1").readText() + URL(SITE_URL + word.name).readText() else ""
    }

    override fun getExamples() : Set<String> =
        EXAMPLE_PATTERN.findAll(siteBody)
            .map { COMMON_FILTER.replace(it.value, "").trim() }
            .toSet()

    override fun getDefinitions() : Set<String> =
        DEFINITION_PATTERN.findAll(siteBody)
            .map { COMMON_FILTER.replace(it.value, "").trim() }
            .toSet()

    override fun getTranscriptions(): Set<String> =
        TRANSCRIPTION_PATTERN.findAll(siteBody)
            .map { "[${it.value}]" }
            .toSet()
}
