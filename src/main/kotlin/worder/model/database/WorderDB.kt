package worder.model.database

interface WorderDB {
    val trackStats: WorderTrackStats
    val summaryStats: WorderSummaryStats


    val inserter: WorderInsertDB
        get() = throw IllegalStateException("Operation is not supported!")

    val updater: WorderUpdateDB
        get() = throw IllegalStateException("Operation is not supported!")
}
