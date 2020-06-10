package worder.views

import javafx.geometry.Pos
import tornadofx.View
import tornadofx.hbox
import tornadofx.plusAssign
import worder.controllers.DatabaseController
import worder.controllers.DatabaseEventListener
import worder.model.database.WorderDB
import worder.views.fragments.StatsBlockFragment

class DashboardView : View(), DatabaseEventListener {
    private val databaseController: DatabaseController by inject()

    override val root = hbox(alignment = Pos.TOP_CENTER)


    init {
        databaseController.subscribe(this)
    }


    override fun onDatabaseConnection(db: WorderDB) {
        this += find<StatsBlockFragment>("stats" to databaseController.stats, "prettyNames" to mapOf(
                "db" to "Data source",
                "updateDb" to "Update Database",
                "insertDb" to "Insert Database",
                "isConnected" to "Connected",
                "timerValue" to "Session duration"
        ))

        this += find<StatsBlockFragment>("stats" to db.summaryStats, "prettyNames" to mapOf(
                "totalAmount" to "Total amount",
                "unlearned" to "Unlearned",
                "learned" to "Learned"
        ))

        this += find<StatsBlockFragment>("stats" to db.trackStats, "prettyNames" to mapOf(
                "totalAffected" to "Total affected",
                "totalInserted" to "Total inserted",
                "totalReset" to "Total reset",
                "totalUpdated" to "Total updated"
        ))

        this += find<StatsBlockFragment>("stats" to db.inserter.inserterSessionStats, "prettyNames" to mapOf(
                "totalProcessed" to "Total processed",
                "inserted" to "Inserted",
                "reset" to "Reset"
        ))

        this += find<StatsBlockFragment>("stats" to db.updater.updaterSessionStats, "prettyNames" to mapOf(
                "totalProcessed" to "Total processed",
                "removed" to "Removed",
                "updated" to "Updated",
                "skipped" to "Skipped",
                "learned" to "Learned"
        ))
    }

    override fun onDatabaseDisconnection() {
        root.children.clear()
    }
}
