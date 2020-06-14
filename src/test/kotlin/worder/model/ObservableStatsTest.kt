package worder.model

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ObservableStatsSyncTest : ShouldSpec({
    class TestStats : BaseObservableStats("Testing Stats Object") {
        var counter: Int by bindToStats(0)
    }


    val stats = TestStats()
    val syncListener = object : TestListener {
        var started = 0L


        override suspend fun beforeTest(testCase: TestCase) {
            stats.counter = 0
            started = System.currentTimeMillis()
        }

        override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            println("${testCase.description.name.name}: { counter=${stats.counter}, time=${System.currentTimeMillis() - started} }")
        }
    }


    defaultTestConfig = TestCaseConfig(invocations = 3)
    listeners(syncListener)


    @Suppress("BlockingMethodInNonBlockingContext")
    should("synchronize using mutex") {
        runBlocking(Dispatchers.Default) {
            val mutex = Mutex()

            repeat(100_000) {
                launch {
                    mutex.withLock { stats.counter++ }
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    should("synchronize using synchronized function") {
        runBlocking(Dispatchers.Default) {
            repeat(100_000) {
                launch {
                    synchronized(stats) { stats.counter++ }
                }
            }
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    should("synchronize using confide coroutine + channel") {
        runBlocking(Dispatchers.Default) {
            val channel = Channel<TestStats.() -> Unit>(capacity = Channel.UNLIMITED)

            val statsJob = launch {
                for (statsUpdate in channel) {
                    statsUpdate.invoke(stats)
                }
            }

            val increasers = launch {
                repeat(100_000) {
                    launch {
                        channel.send { counter++ }
                    }
                }
            }

            increasers.join()
            channel.close()
            statsJob.join()
        }
    }
})
