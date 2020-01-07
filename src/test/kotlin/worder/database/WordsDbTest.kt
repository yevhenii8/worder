package worder.database

import org.junit.Test
import worder.database.sqllite.SqlLiteFileInserter
import worder.database.sqllite.SqlLiteFileUpdater

class WordsDbTest {
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

    private fun worderTrackTest(wordsDb: WordsDb) {
        println(wordsDb.worderTrack)
    }

    private fun summaryTest(wordsDb: WordsDb) {
        println(wordsDb.summary)
    }

    private fun sessionStatTest(wordsDb: WordsDb) {
        println(wordsDb.sessionStat)
    }

    private fun allStatsTest(wordsDb: WordsDb) {
        for(dbStat in wordsDb.allStats)
            println(dbStat)
    }
}
