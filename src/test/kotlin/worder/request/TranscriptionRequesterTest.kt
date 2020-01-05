package worder.request

import org.junit.Test
import worder.model.BaseWord
import worder.model.Word

class TranscriptionRequesterTest {
    @Test
    fun generalTestingAll() {
        val requesters = Requester.getAllDefaultImplementations()
            .filterIsInstance<TranscriptionRequester>()
        val word =  BaseWord("tyranny")

        println()
        println()

        for (requester in requesters)
            requester.acceptWord(word).run {
                println("$requester: ${requester.getTranscriptions()}")
            }

        println()
        println()
    }
}
