package worder.request

import worder.model.Word


open class RequesterStatDecorator(private val requester: Requester) : Requester {
    var definitions: Int? = null
    var translations: Int? = null
    var examples: Int? = null
    var transcriptions: Int? = null

    override val sessionStat: RequesterStat
        get() = RequesterStat(
            definitions =definitions,
            translations = translations,
            examples = examples,
            transcriptions = transcriptions
        )


    override fun acceptWord(word: Word) = requester.acceptWord(word)
    override fun toString(): String  = requester.javaClass.simpleName


    /*
    actually overridden methods below
    don't forget to specify which of them your class actually implements when inherit from it
    DefinitionRequester, TranslationRequester, ExampleRequester, TranscriptionRequester
     */

    fun getDefinitions(): Set<String> {
        return if (requester is DefinitionRequester)
            requester.getDefinitions().also { definitions = (definitions ?: 0).plus(it.size) }
        else throw IllegalStateException("You can't use this requester as DefinitionRequester")
    }

    fun getTranslations(): Set<String> {
        return if (requester is TranslationRequester)
            requester.getTranslations().also { translations = (translations ?: 0).plus(it.size) }
        else throw IllegalStateException("You can't use this requester as TranslationRequester")
    }

    fun getExamples(): Set<String> {
        return if (requester is ExampleRequester)
            requester.getExamples().also { examples = (examples ?: 0).plus(it.size) }
        else throw IllegalStateException("You can't use this requester as ExampleRequester")
    }

    fun getTranscriptions(): Set<String> {
        return if (requester is TranscriptionRequester)
            requester.getTranscriptions().also { transcriptions = (transcriptions ?: 0).plus(it.size) }
        else throw IllegalStateException("You can't use this requester as TranscriptionRequester")
    }
}
