package worder.model.database

interface WorderDB {
    val worderTrack: WorderTrackStats
    val summaryStat: DatabaseSummaryStats
}
