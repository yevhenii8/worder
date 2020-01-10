package worder.database


interface LocalWordsDb {
    val worderTrack: DbWorderTrack
    val summary: DbSummary
    val sessionStat: DbStat
}
