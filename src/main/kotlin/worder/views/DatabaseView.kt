package worder.views

import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER
import javafx.scene.Parent
import javafx.scene.input.TransferMode
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import tornadofx.View
import tornadofx.action
import tornadofx.addClass
import tornadofx.button
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.plusAssign
import tornadofx.vbox
import worder.controllers.DatabaseController
import worder.views.fragments.StatsBlockFragment
import worder.views.styles.WorderStyle

class DatabaseView : View("Database") {
    private val databaseController: DatabaseController by inject()
    private val dashboardView: DashboardView by inject()


    override val root: Parent = find<ConnectionView>().root


    fun onConnect() {
        find<ConnectionView>().replaceWith<DisconnectionView>()

        dashboardView.apply {
            this += find<StatsBlockFragment>("stats" to databaseController.stats, "prettyNames" to mapOf(
                    "db" to "Worder Database",
                    "updateDb" to "Update Database",
                    "insertDb" to "Insert Database",
                    "isConnected" to "Connected"
            ))

            this += find<StatsBlockFragment>("stats" to databaseController.db!!.summaryStats, "prettyNames" to mapOf(
                    "totalAmount" to "Total amount",
                    "unlearned" to "Unlearned",
                    "learned" to "Learned"
            ))

            this += find<StatsBlockFragment>("stats" to databaseController.db!!.trackStats, "prettyNames" to mapOf(
                    "totalAffected" to "Total affected",
                    "totalInserted" to "Total inserted",
                    "totalReset" to "Total reset",
                    "totalUpdated" to "Total updated"
            ))

            this += find<StatsBlockFragment>("stats" to databaseController.insertDb!!.inserterSessionStats, "prettyNames" to mapOf(
                    "totalProcessed" to "Total processed",
                    "inserted" to "Inserted",
                    "reset" to "Reset"
            ))

            this += find<StatsBlockFragment>("stats" to databaseController.updateDb!!.updaterSessionStats, "prettyNames" to mapOf(
                    "totalProcessed" to "Total processed",
                    "removed" to "Removed",
                    "updated" to "Updated",
                    "skipped" to "Skipped",
                    "learned" to "Learned"
            ))
        }
    }

    fun onDisconnect() {
        find<DisconnectionView>().replaceWith<ConnectionView>()
        dashboardView.root.children.clear()
    }
}

class ConnectionView : View() {
    private val databaseController: DatabaseController by inject()

    override val root = vbox {
        label("Please drag & drop here a MyDictionary backup file...")

        imageview(resources.image("/files-lg.png")) {
            VBox.setMargin(this, Insets(20.0, 0.0, 0.0, 0.0))
        }

        setOnDragOver {
            if (it.gestureSource !== this && it.dragboard.hasFiles())
                it.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
            it.consume()
        }

        setOnDragDropped {
            if (it.dragboard.hasFiles())
                databaseController.connectToSqlLiteFile(it.dragboard.files.toString())
            it.consume()
        }

        addClass(WorderStyle.dragDropField)
    }
}

class DisconnectionView : View() {
    private val databaseController: DatabaseController by inject()
    private val label = label()

    override val root = vbox(alignment = CENTER) {
        hbox(alignment = CENTER) {
            add(label)

            imageview(resources.image("/done-icon.png")) {
                HBox.setMargin(this, Insets(0.0, 0.0, 0.0, 10.0))
            }
        }

        button("Disconnect") {
            VBox.setMargin(this, Insets(15.0, 0.0, 0.0, 0.0))

            action {
                databaseController.disconnect()
            }

            setPrefSize(400.0, 75.0)
        }
    }

    override fun onDock() {
        super.onDock()
        label.text = "Connected to ${databaseController.db}"
    }
}
