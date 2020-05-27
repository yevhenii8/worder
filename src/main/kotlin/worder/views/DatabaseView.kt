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
import tornadofx.vbox
import worder.controllers.DatabaseController

class DatabaseView : View("Database") {
    override val root: Parent = find<ConnectionView>().root
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
