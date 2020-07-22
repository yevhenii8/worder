/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WebsiteRequesterDecoratorTest.kt>
 * Created: <22/07/2020, 09:12:45 PM>
 * Modified: <22/07/2020, 09:41:48 PM>
 * Version: <16>
 */

package worder.update.model

import io.kotest.core.spec.style.ShouldSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import worder.core.log
import worder.core.model.BareWord
import worder.update.model.impl.WebsiteRequesterDecorator
import worder.update.model.impl.requesters.CambridgeRequester

class WebsiteRequesterDecoratorTest : ShouldSpec({
    val word = BareWord("hello")


    xshould("guarantee requests interval preservation") {
        val requester = object : Requester {
            override suspend fun requestWord(word: BareWord) {
                log("requestWord() has been called!")
            }
        }
        val decoratedRequester = object : WebsiteRequesterDecorator(requester) {}

        withContext(Dispatchers.Default) {
            repeat(5) {
                launch {
                    decoratedRequester.requestWord(word)
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    should("unveil number of coroutines created for one request with a real requester") {
        val realRequester = CambridgeRequester.instance

        runBlocking {
            launch {
                log("Before realRequester.requestWord(word)")
                realRequester.requestWord(word)
                log("After realRequester.requestWord(word)")
            }

            launch {
                log("new coroutine's been created!")
            }
        }
    }
})
