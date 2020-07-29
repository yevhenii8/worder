/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <FakeRequester.kt>
 * Created: <24/07/2020, 07:45:55 PM>
 * Modified: <28/07/2020, 08:12:36 PM>
 * Version: <20>
 */

package worder.update.model.impl.requesters

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleBooleanProperty
import kotlinx.coroutines.delay
import worder.core.model.BareWord
import worder.update.model.DefinitionRequester
import worder.update.model.ExampleRequester
import worder.update.model.ObservableRequesterStats
import worder.update.model.Requester
import worder.update.model.TranscriptionRequester
import worder.update.model.TranslationRequester
import worder.update.model.impl.SimpleRequesterStats
import worder.update.model.impl.WebsiteRequesterDecorator

class FakeRequester : DefinitionRequester, ExampleRequester, TranscriptionRequester, TranslationRequester {
    companion object {
        val instance: Requester by lazy {
            object : WebsiteRequesterDecorator(FakeRequester()), DefinitionRequester, ExampleRequester, TranscriptionRequester, TranslationRequester {}
        }
    }


    override val isBusy: Boolean = false
    override val isBusyProperty: ReadOnlyProperty<Boolean> = SimpleBooleanProperty()
    override val observableStats: ObservableRequesterStats = SimpleRequesterStats("Fake Requester")
    override val definitions: List<String> = listOf(
            "a place regarded in various religions as a spiritual realm of evil and suffering",
            "used to express annoyance or surprise or for emphasis",
            "In religion and folklore, Hell is an afterlife location in which evil souls are subjected to punitive suffering, often torture as eternal punishment after deathhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh hell end"
    )
    override val examples: List<String> = listOf(
            "I've been through hell",
            "hell, no, we were all we were allwe were allwe werewe were allwe were allwe were allwe were allwe were allwe were all allwe were allwe were allwe were allwe were allwe were all ---------------- hell end"
    )
    override val translations: List<String> = listOf(
            "ад",
            "преисподня",
            "притон",
            "игорный дом"
    )
    override val transcriptions: List<String> = listOf(
            "hel"
    )


    override suspend fun requestWord(word: BareWord) {
        delay(2000)
    }
}