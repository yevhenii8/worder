package worder.views

import javafx.geometry.Pos
import tornadofx.View
import tornadofx.addChildIfPossible
import tornadofx.hbox
import worder.controllers.DatabaseController
import worder.controllers.DatabaseEventListener
import worder.model.database.WorderDB
import worder.views.fragments.ObservableStatsFragment

class DashboardView : View(), DatabaseEventListener {
    private val databaseController: DatabaseController by inject()

    override val root = hbox(alignment = Pos.TOP_CENTER)


    init {
        databaseController.subscribe(this)
    }


    override fun onDatabaseConnection(db: WorderDB) {
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to databaseController.observableStats).root)
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to db.summaryStats).root)
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to db.trackStats).root)
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to db.inserter.inserterStats).root)
        root.addChildIfPossible(find<ObservableStatsFragment>("observableStats" to db.updater.updaterStats).root)
    }

    override fun onDatabaseDisconnection() {
        root.children.clear()
    }
}
