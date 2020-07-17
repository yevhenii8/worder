/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <BaseObservableStatsTest.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <17/07/2020, 07:21:19 PM>
 * Version: <6>
 */

package worder.core.model

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BaseObservableStatsTest : ShouldSpec({
    @Suppress("BlockingMethodInNonBlockingContext")
    should("perform concurrent operation synchronized") {
        val times = 100_000
        val stats = object : BaseObservableStats("Testing Stats Object") {
            var counter: Int by bindThroughValue(0)
        }

        runBlocking(Dispatchers.Default) {
            repeat(times) {
                launch { stats.counter++ }
            }
        }

        stats.counter shouldNotBe times
        stats.counter = 0

        runBlocking(Dispatchers.Default) {
            repeat(times) {
                launch { stats.applyWithMainUI { counter++ } }
            }
        }

        stats.counter shouldBe times
    }
})
