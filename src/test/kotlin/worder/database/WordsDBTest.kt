package worder.database

import org.junit.Test
import worder.database.sqllite.SqlLiteFileInserter
import worder.database.sqllite.SqlLiteFileInserterTest
import worder.database.sqllite.SqlLiteFileUpdater

class WordsDBTest {
    private val pathToSampleDb = "/home/yevhenii/IdeaProjects/worder_deprecated/updated.bck"
    private val testedImplementations = listOf(
        SqlLiteFileInserter::class,
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

    private fun worderTrackTest(wordsDb: WordsDB) {
        println(wordsDb.worderTrack)
    }

    private fun summaryTest(wordsDb: WordsDB) {
        println(wordsDb.summary)
    }

    private fun sessionStatTest(wordsDb: WordsDB) {
        println(wordsDb.sessionStat)
    }

    private fun allStatsTest(wordsDb: WordsDB) {
        for(dbStat in wordsDb.allStats)
            println(dbStat)
    }
}
