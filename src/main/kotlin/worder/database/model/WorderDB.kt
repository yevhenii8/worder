package worder.database.model

interface WorderDB {
    val trackStats: WorderTrackStats
    val summaryStats: WorderSummaryStats


    val inserter: WorderInsertDB
        get() = error("Operation is not supported!")

    val updater: WorderUpdateDB
        get() = error("Operation is not supported!")
}
