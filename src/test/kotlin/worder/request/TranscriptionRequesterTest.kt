package worder.request

import org.junit.Test
import worder.BaseWord

class TranscriptionRequesterTest {
    @Test
    fun generalTestingAll() {
        val requesters = Requester.allDefaultImplementations()
            .filterIsInstance<TranscriptionRequester>()
        val word = BaseWord("tyranny")

        println()
        println()

        for (requester in requesters)
            requester.requestWord(word).run {
                println("$requester: ${requester.getTranscriptions()}")
            }

        println()
        println()
    }
}
