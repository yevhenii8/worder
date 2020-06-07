package worder.model.insert.request

import worder.model.Word


interface Requester {
//    val sessionStat: RequesterSessionStat
//        get() = throw IllegalStateException("Should be used RequesterStatDecorator() implementation!")
//
//    val lastRequestStat: RequestStat
//        get() = throw IllegalStateException("Should be used RequesterStatDecorator() implementation!")


    suspend fun requestWord(word: Word)
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
