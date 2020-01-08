package worder.request

import worder.Word


open class RequesterStatDecorator(private val requester: Requester) : Requester {
    private var totalRequests = 0
    private var totalDefinitions: Int? = null
    private var totalTranslations: Int? = null
    private var totalExamples: Int? = null
    private var totalTranscriptions: Int? = null


    private var lastWord: String? = null
    private var lastDefinitions: Int? = null
    private var lastTranslations: Int? = null
    private var lastExamples: Int? = null
    private var lastTranscriptions: Int? = null


    override val sessionStat: RequesterSessionStat
        get() = RequesterSessionStat(
            origin = requester.javaClass.simpleName,
            totalRequests = totalRequests,
            totalDefinitions = totalDefinitions,
            totalTranslations = totalTranslations,
            totalExamples = totalExamples,
            totalTranscriptions = totalTranscriptions
        )

    override val lastRequestStat: RequestStat
        get() = if (lastWord != null) RequestStat(
            origin = requester.javaClass.simpleName,
            word = lastWord!!,
            definitions = lastDefinitions,
            translations = lastTranslations,
            examples = lastExamples,
            transcriptions = lastTranscriptions
        ) else throw IllegalStateException("You should at least once call requestWord()")

    override suspend fun requestWord(word: Word) {
        requester.requestWord(word)
        totalRequests++
        lastWord = word.name

        lastDefinitions = if (requester is DefinitionRequester) requester.definitions.count() else null
        lastTranslations = if (requester is TranslationRequester) requester.translations.count() else null
        lastExamples = if (requester is ExampleRequester) requester.examples.count() else null
        lastTranscriptions = if (requester is TranscriptionRequester) requester.transcriptions.count() else null

        totalDefinitions = lastDefinitions?.plus(totalDefinitions ?: 0)
        lastTranslations = lastTranslations?.plus(lastTranslations ?: 0)
        totalExamples = lastExamples?.plus(lastExamples ?: 0)
        totalTranscriptions = lastTranscriptions?.plus(lastTranscriptions ?: 0)
    }


    override fun toString(): String = requester.javaClass.simpleName
    override fun hashCode(): Int = requester.hashCode()
    override fun equals(other: Any?): Boolean = requester == other


    /*
    actually overridden methods below
    don't forget to specify which of them your class actually implements when inherit from it
    DefinitionRequester, TranslationRequester, ExampleRequester, TranscriptionRequester
     */

    val definitions: Set<String> = if (requester is DefinitionRequester) requester.definitions
    else throw IllegalStateException("You can't use this requester as DefinitionRequester")

    val translations: Set<String> = if (requester is TranslationRequester) requester.translations
    else throw IllegalStateException("You can't use this requester as TranslationRequester")

    val examples: Set<String> = if (requester is ExampleRequester) requester.examples
    else throw IllegalStateException("You can't use this requester as ExampleRequester")

    val transcriptions: Set<String> = if (requester is TranscriptionRequester) requester.transcriptions
    else throw IllegalStateException("You can't use this requester as TranscriptionRequester")
}
