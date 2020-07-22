/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <ConcurrentCommitTest.kt>
 * Created: <17/07/2020, 11:29:59 PM>
 * Modified: <22/07/2020, 05:48:19 PM>
 * Version: <35>
 */

package worder.insert.model

import io.kotest.core.spec.style.ShouldSpec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import worder.core.formatGrouped
import worder.core.round
import worder.database.model.impl.SQLiteFile
import worder.insert.model.impl.DefaultInsertBatch
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.math.roundToInt
import kotlin.streams.toList
import kotlin.system.measureTimeMillis
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ConcurrentCommitTest : ShouldSpec({
    val samplePath: Path = Path.of("stuff").resolve("sample")
    val originalSampleDB: File = samplePath.resolve("sample-db.bck").toFile()
    val sampleDB: File = samplePath.resolve("sample-db_TMP.bck").toFile()


    suspend fun concurrentCommit(filesCount: Int) {
        originalSampleDB.copyTo(sampleDB, overwrite = true)

        val worderDb = SQLiteFile.createInstance(sampleDB)
        val insertModel = DefaultInsertBatch.createInstance(
                worderDb.inserter,
                Files.list(Path.of("/home/yevhenii/Other/inserter")).map { it.toFile() }.toList().take(filesCount)
        )

        insertModel.commitAllUnits()
    }


    @Suppress("BlockingMethodInNonBlockingContext")
    xshould("compare multi/single thread performance with sqlite").config(timeout = Duration.INFINITE) {
        val filesCounts = listOf(40, 20, 10, 5, 1)
        val runs = 3

        filesCounts.forEach { filesCount ->
            val singleRes = mutableListOf<Long>()
            val multiRes = mutableListOf<Long>()
            val differs = mutableListOf<Double>()


            repeat(runs) {
                val blockingTime = measureTimeMillis {
                    runBlocking {
                        concurrentCommit(filesCount)
                    }
                }

                singleRes.add(blockingTime)
            }

            repeat(runs) {
                val concurrentTime = measureTimeMillis {
                    val job = launch(Dispatchers.Default) {
                        concurrentCommit(filesCount)
                    }
                    job.join()
                }

                multiRes.add(concurrentTime)
            }


            println("running $filesCount files $runs times")
            println("single thread  --------------  multi thread |")

            repeat(runs) { run ->
                val single = singleRes[run].formatGrouped().padEnd(13)
                val multi = multiRes[run].formatGrouped().padEnd(12)
                val diff = (100 - multiRes[run].toDouble() / singleRes[run] * 100).round(2).also {
                    differs.add(it)
                }

                println("$single  --------------  $multi | ~ $diff%")
            }

            val singleAvg = singleRes.average().roundToInt().formatGrouped().padEnd(13)
            val multiAvg = multiRes.average().roundToInt().formatGrouped().padEnd(12)
            val differsAvg = differs.average().round(2)

            println("--------------------------------------------|---------")
            println("$singleAvg  --------------  $multiAvg | ~ $differsAvg%")
            println()
            println()
        }

        sampleDB.delete()
    }

    should("compare multi thread performance with sqlite").config(timeout = Duration.INFINITE) {
        val filesCounts = listOf(40, 20, 10, 5, 1)
        val runs = 3

        filesCounts.forEach { filesCount ->
            val results = mutableListOf<Long>()

            repeat(runs) {
                val concurrentTime = measureTimeMillis {
                    val job = launch(Dispatchers.Default) {
                        concurrentCommit(filesCount)
                    }
                    job.join()
                }

                results.add(concurrentTime)
            }


            println("running $filesCount files $runs times")
            results.forEach {
                println(" * ${it.formatGrouped()} ms")
            }
            println()
        }

        sampleDB.delete()
    }
})
