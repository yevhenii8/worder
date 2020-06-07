package worder.model.insert.request

import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.sync.*
import worder.model.Word
import java.io.IOException
import java.net.URI
import java.net.http.*
import java.util.concurrent.atomic.AtomicLong


/*
    Implements common behavior for website-based requester:
     * takes on itself statistics-keeping (sessionStat && requestStat)
     * prevents from site being requested too often, it forces object to stick to min REQUEST_INTERVAL
     * provides REQUEST_TIMEOUT and forces object to stick to it
     * prevents object.request() from being concurrently executed
 */

open class RequesterBaseDecorator(private val requester: Requester) : Requester {
    companion object {
        private const val REQUEST_INTERVAL = 3000L
        private const val REQUEST_TIMEOUT = 5000L

        private val client: HttpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build()

        suspend fun Word.sendGetRequest(url: String): String {
            return if (!name.contains(" ")) {
                val request = HttpRequest.newBuilder(URI.create(url)).build()
                val response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).await()

                if (response.statusCode() != 200)
                    throw IOException("HTTP request failed with code ${response.statusCode()}")

                response.body()!!
            } else
                ""
        }
    }


    private val mutex = Mutex()
    private var lastRequestTime = AtomicLong()


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


//    override val sessionStat: RequesterSessionStat
//        get() = RequesterSessionStat(
//            origin = requester.javaClass.simpleName,
//            totalRequests = totalRequests,
//            totalDefinitions = totalDefinitions,
//            totalTranslations = totalTranslations,
//            totalExamples = totalExamples,
//            totalTranscriptions = totalTranscriptions
//        )
//
//    override val lastRequestStat: RequestStat
//        get() = if (lastWord != null) RequestStat(
//            origin = requester.javaClass.simpleName,
//            word = lastWord!!,
//            definitions = lastDefinitions,
//            translations = lastTranslations,
//            examples = lastExamples,
//            transcriptions = lastTranscriptions
//        ) else throw IllegalStateException("You should at least once call requestWord()")


    override suspend fun requestWord(word: Word) {
        mutex.withLock {
            val timeDiff = System.currentTimeMillis() - lastRequestTime.get()
            if (timeDiff < REQUEST_INTERVAL)
                delay(REQUEST_INTERVAL - timeDiff)

            println("$requester: requesting $word")
            withTimeout(REQUEST_TIMEOUT) { requester.requestWord(word) }
        }

        lastRequestTime.set(System.currentTimeMillis())
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

    val definitions: Set<String>
        get() = if (requester is DefinitionRequester) requester.definitions
        else throw IllegalStateException("You can't use this requester as DefinitionRequester")

    val translations: Set<String>
        get() = if (requester is TranslationRequester) requester.translations
        else throw IllegalStateException("You can't use this requester as TranslationRequester")

    val examples: Set<String>
        get() = if (requester is ExampleRequester) requester.examples
        else throw IllegalStateException("You can't use this requester as ExampleRequester")

    val transcriptions: Set<String>
        get() = if (requester is TranscriptionRequester) requester.transcriptions
        else throw IllegalStateException("You can't use this requester as TranscriptionRequester")
}
