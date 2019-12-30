package worder.request

import worder.model.Word
import worder.request.implementations.Cambridge
import worder.request.implementations.Lingvo
import worder.request.implementations.Macmillan
import worder.request.implementations.WooordHunt


interface Requester {
    val sessionStat: RequesterStat
        get() = throw IllegalStateException("Should be used decorator's implementation!")

    fun acceptWord(word: Word)

    companion object {
        fun getAllDefaultImplementations() : Set<Requester> = setOf(
            Lingvo.newInstance(),
            Macmillan.newInstance(),
            WooordHunt.newInstance(),
            Cambridge.newInstance()
        )
    }
}


interface RequesterProducer {
     /*
     has to use RequesterStatDecorator() in order to obtain out-of-box stat keeping functionality
     it's been created only in order to unify stat structure and
     incapsulate its boilerplate code from directly requester implementing
      */

    fun newInstance() : Requester
}


interface DefinitionRequester : Requester { fun getDefinitions() : Set<String> }
interface TranslationRequester : Requester { fun getTranslations() : Set<String> }
interface ExampleRequester : Requester { fun getExamples() : Set<String> }
interface TranscriptionRequester : Requester { fun getTranscriptions() : Set<String> }
