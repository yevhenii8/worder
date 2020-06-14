package worder.model

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class BaseObservableStatsTest : ShouldSpec({
    @Suppress("BlockingMethodInNonBlockingContext")
    should("Perform concurrent operation synchronized") {
        val times = 100_000
        val stats = object : BaseObservableStats("Testing Stats Object") {
            var counter: Int by bindToStats(0)
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
                launch { stats.applySynchronized { counter++ } }
            }
        }

        stats.counter shouldBe times
    }
})
