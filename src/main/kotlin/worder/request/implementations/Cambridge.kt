package worder.request.implementations

import worder.model.Word
import worder.request.*
import java.net.URL


class Cambridge private constructor() : DefinitionRequester, ExampleRequester {
    companion object : RequesterProducer {
        private const val SITE_URL = "https://dictionary.cambridge.org/dictionary/english/"

        private val DEFINITION_PATTERN = Regex("(?<=<div class=\"def ddef_d db\">).*?(?=</div>)")
        private val EXAMPLE_PATTERN = Regex("(?<=<span class=\"eg deg\">).*?(?=</span></div>)")
        private val COMMON_FILTER = Regex("(<.*?>)|(: )")

        override fun newInstance(): Requester =
            object : RequesterStatDecorator(Cambridge()), DefinitionRequester, ExampleRequester { }
        }


    private var siteBody = ""

    override fun acceptWord(word: Word) {
        siteBody = if (!word.name.contains(" ")) URL(SITE_URL + word.name).readText() else ""
    }

    override fun getDefinitions(): Set<String> =
        DEFINITION_PATTERN.findAll(siteBody)
            .map { COMMON_FILTER.replace(it.value, "").trim() }
            .toSet()

    override fun getExamples(): Set<String> =
        EXAMPLE_PATTERN.findAll(siteBody)
            .map { COMMON_FILTER.replace(it.value, "").trim() }
            .toSet()
}
