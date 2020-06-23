package worder.database.model

interface WorderDB {
    val observableTrackStats: ObservableWorderTrackStats
    val observableSummaryStats: ObservableWorderSummaryStats


    val inserter: WorderInsertDB
        get() = error("Operation is not supported!")

    val updater: WorderUpdateDB
        get() = error("Operation is not supported!")
}
