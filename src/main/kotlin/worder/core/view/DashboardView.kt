/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DashboardView.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <16/07/2020, 09:43:34 PM>
 * Version: <18>
 */

package worder.core.view

import javafx.geometry.Pos
import tornadofx.View
import tornadofx.hbox
import tornadofx.paddingVertical
import worder.database.DatabaseController
import worder.database.DatabaseEventListener
import worder.database.model.WorderDB

class DashboardView : View(), DatabaseEventListener {
    private val databaseController: DatabaseController by inject()


    override val root = hbox(spacing = 30, alignment = Pos.TOP_CENTER) {
        paddingVertical = 30
    }


    init {
        databaseController.subscribe(this)
    }


    override fun onDatabaseConnection(db: WorderDB) {
        root.add(
                observableStats(
                        stats = databaseController.observableStats,
                        valueMutators = mapOf("Data source" to { value: Any? -> value.toString().substringAfterLast("/") })
                )

        )

        root.add(observableStats(db.observableSummaryStats))
        root.add(observableStats(db.observableTrackStats))
        root.add(observableStats(db.inserter.observableInserterStats))
        root.add(observableStats(db.updater.observableUpdaterStats))
    }

    override fun onDatabaseDisconnection() {
        root.children.clear()
    }
}
