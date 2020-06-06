package worder.views

import tornadofx.View
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.plusAssign
import worder.controllers.DatabaseController
import worder.controllers.DatabaseListener
import worder.views.fragments.StatsBlockFragment
import worder.views.styles.WorderStyle

class DashboardView : View(), DatabaseListener {
    private val databaseController: DatabaseController by inject()

    init {
        databaseController.subscribe(this)
    }

    override val root = hbox {
        addClass(WorderStyle.dashboard)
    }

    override fun onDatabaseConnection() {
        val database = databaseController.db!!

        this += find<StatsBlockFragment>("stats" to databaseController.stats, "prettyNames" to mapOf(
                "db" to "Data source",
                "updateDb" to "Update Database",
                "insertDb" to "Insert Database",
                "isConnected" to "Connected",
                "timerValue" to "Session duration"
        ))

        this += find<StatsBlockFragment>("stats" to database.summaryStats, "prettyNames" to mapOf(
                "totalAmount" to "Total amount",
                "unlearned" to "Unlearned",
                "learned" to "Learned"
        ))

        this += find<StatsBlockFragment>("stats" to database.trackStats, "prettyNames" to mapOf(
                "totalAffected" to "Total affected",
                "totalInserted" to "Total inserted",
                "totalReset" to "Total reset",
                "totalUpdated" to "Total updated"
        ))

        this += find<StatsBlockFragment>("stats" to database.inserter.inserterSessionStats, "prettyNames" to mapOf(
                "totalProcessed" to "Total processed",
                "inserted" to "Inserted",
                "reset" to "Reset"
        ))

        this += find<StatsBlockFragment>("stats" to database.updater.updaterSessionStats, "prettyNames" to mapOf(
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
