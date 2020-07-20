/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <Requester.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <20/07/2020, 11:27:45 PM>
 * Version: <5>
 */

package worder.update.model

import worder.core.model.Word
import worder.update.model.impl.requesters.CambridgeRequester
import worder.update.model.impl.requesters.LingvoRequester
import worder.update.model.impl.requesters.MacmillanRequester
import worder.update.model.impl.requesters.WooordHuntRequester

interface Requester {
    companion object {
        val defaultRequesters: List<Requester> = listOf(
                CambridgeRequester.instance,
                LingvoRequester.instance,
                MacmillanRequester.instance,
                WooordHuntRequester.instance
        )
    }


    val observableStats: ObservableRequesterStats
        get() = throw IllegalStateException("Not implemented for this requester!")


    suspend fun requestWord(word: Word)
}


// every requester cal implement on or more of the interfaces below
// use contract is single-threaded: request -> pick result -> request -> pick result...

interface DefinitionRequester : Requester {
    val definitions: List<String>
}

interface TranslationRequester : Requester {
    val translations: List<String>
}

interface ExampleRequester : Requester {
    val examples: List<String>
}

interface TranscriptionRequester : Requester {
    val transcriptions: List<String>
}
