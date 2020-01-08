package worder.request.implementations

import worder.Word
import worder.request.*


class Cambridge private constructor() : DefinitionRequester, ExampleRequester {
    companion object : RequesterProducer {
        private const val SITE_URL = "https://dictionary.cambridge.org/dictionary/english/"

        private val DEFINITION_PATTERN = Regex("(?<=<div class=\"def ddef_d db\">).*?(?=</div>)")
        private val EXAMPLE_PATTERN = Regex("(?<=<span class=\"eg deg\">).*?(?=</span></div>)")
        private val COMMON_FILTER = Regex("(<.*?>)|(: )")

        override fun newInstance(): Requester =
            object : RequesterStatDecorator(Cambridge()), DefinitionRequester, ExampleRequester {}
    }


    override lateinit var definitions: Set<String>
        private set

    override lateinit var examples: Set<String>
        private set


    override suspend fun requestWord(word: Word) {
        val body = word.sendAsyncRequest(SITE_URL + word.name)

        definitions = DEFINITION_PATTERN.findAll(body)
            .map { COMMON_FILTER.replace(it.value, "").trim() }
            .toSet()

        examples = EXAMPLE_PATTERN.findAll(body)
            .map { COMMON_FILTER.replace(it.value, "").trim() }
            .toSet()
    }
}
