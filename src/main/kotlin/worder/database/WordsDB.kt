package worder.database

interface WordsDB {
    val worderTrack: DbWorderTrack
    val summary: DbSummary
    val sessionStat: DbStat

    val allStats
        get() = listOf(sessionStat, worderTrack, summary)
}
