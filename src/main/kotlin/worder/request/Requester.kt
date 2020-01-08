package worder.request

import worder.Word


interface Requester {
    val sessionStat: RequesterSessionStat
        get() = throw IllegalStateException("Should be used RequesterStatDecorator() implementation!")

    val lastRequestStat: RequestStat
        get() = throw IllegalStateException("Should be used RequesterStatDecorator() implementation!")


    suspend fun requestWord(word: Word)
}


interface RequesterProducer {
    /*
    Requester has to use RequesterStatDecorator() in order to obtain out-of-box stat's keeping functionality
    it's been created only in order to unify stat structure and
    incapsulate its boilerplate code from directly requester implementing
     */

    fun newInstance(): Requester
}


interface DefinitionRequester : Requester {
    val definitions: Set<String>
}

interface TranslationRequester : Requester {
    val translations: Set<String>
}

interface ExampleRequester : Requester {
    val examples: Set<String>
}

interface TranscriptionRequester : Requester {
    val transcriptions: Set<String>
}
