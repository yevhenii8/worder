package worder.core.view

import javafx.geometry.Pos
import tornadofx.View
import tornadofx.addChildIfPossible
import tornadofx.hbox
import worder.database.DatabaseController
import worder.database.DatabaseEventListener
import worder.database.model.WorderDB

class DashboardView : View(), DatabaseEventListener {
    private val databaseController: DatabaseController by inject()

    override val root = hbox(alignment = Pos.TOP_CENTER)


    init {
        databaseController.subscribe(this)
    }


    override fun onDatabaseConnection(db: WorderDB) {
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to databaseController.observableStats).root)
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to db.observableSummaryStats).root)
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to db.observableTrackStats).root)
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to db.inserter.observableInserterStats).root)
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to db.updater.observableUpdaterStats).root)
    }

    override fun onDatabaseDisconnection() {
        root.children.clear()
    }
}