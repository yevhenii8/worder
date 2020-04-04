package worder.database

import org.junit.Test
import worder.database.sqllite.SqlLiteFileExtracter
import worder.database.sqllite.SqlLiteFileUpdater

class LocalWordsDbTest {
    private val pathToSampleDb = "/home/yevhenii/IdeaProjects/worder_deprecated/updated.bck"
    private val testedImplementations = listOf(
        SqlLiteFileExtracter::class,
        SqlLiteFileUpdater::class
    )

    @Test
    fun testAll() {
        for(wordsDb in testedImplementations) {
            val obj = wordsDb.constructors.first().call(pathToSampleDb)
            worderTrackTest(obj)
            summaryTest(obj)
            sessionStatTest(obj)
            allStatsTest(obj)
            println()
        }
    }

    private fun worderTrackTest(wordsDb: LocalWordsDb) {
        println(wordsDb.worderTrack)
    }

    private fun summaryTest(wordsDb: LocalWordsDb) {
        println(wordsDb.summary)
    }

    private fun sessionStatTest(wordsDb: LocalWordsDb) {
        println(wordsDb.sessionStat)
    }

    private fun allStatsTest(wordsDb: LocalWordsDb) {
        for(dbStat in wordsDb.allStats)
            println(dbStat)
    }
}
