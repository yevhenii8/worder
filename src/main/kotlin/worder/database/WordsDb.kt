package worder.database


interface WordsDb {
    val worderTrack: DbWorderTrack
    val summary: DbSummary
    val sessionStat: DbStat

    val allStats
        get() = listOf(sessionStat, worderTrack, summary)
}
