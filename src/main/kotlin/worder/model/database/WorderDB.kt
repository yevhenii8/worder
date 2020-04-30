package worder.model.database

interface WorderDB : WorderInsertDB, WorderUpdateDB {
    val worderTrack: DbWorderTrack
    val summary: DbSummary
}
