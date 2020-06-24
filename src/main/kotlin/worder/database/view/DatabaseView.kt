package worder.database.view

import javafx.geometry.Insets
import javafx.geometry.Pos.CENTER
import javafx.scene.Parent
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser.ExtensionFilter
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.replaceChildren
import tornadofx.stackpane
import tornadofx.vbox
import worder.database.DatabaseController
import worder.database.DatabaseEventListener
import worder.database.model.WorderDB
import worder.core.view.DragAndDropFragment
import java.io.File

class DatabaseView : View("Database"), DatabaseEventListener {
    private val databaseController: DatabaseController by inject()
    private val databaseDisconnectedFragment = find<DragAndDropFragment>(
            "text" to "Drag MyDictionary backup file here to connect to it",
            "windowTitle" to "MyDictionary Backup File Selection",
            "extensionFilter" to ExtensionFilter("Backup File (*.bck)", "*.bck"),
            "action" to { files: List<File> -> databaseController.connectToSqlLiteFile(files.first()) },
            "allowMultiselect" to false
    ).root

    override val root: Parent = stackpane {
        add(databaseDisconnectedFragment)
    }


    init {
        databaseController.subscribe(this)
    }


    override fun onDatabaseConnection(db: WorderDB) {
        root.replaceChildren(find<DatabaseConnectedView>().root)
    }

    override fun onDatabaseDisconnection() {
        root.replaceChildren(databaseDisconnectedFragment)
    }
}

class DatabaseConnectedView : View() {
    private val databaseController: DatabaseController by inject()
    private val label = label()

    override val root = vbox(alignment = CENTER) {
        hbox(alignment = CENTER) {
            add(label)

            imageview("/icons/blue-done-icon_24x24.png") {
                HBox.setMargin(this, Insets(0.0, 0.0, 0.0, 10.0))
            }
        }

        button("Disconnect") {
            VBox.setMargin(this, Insets(15.0))

            action {
                databaseController.disconnect()
            }

            setPrefSize(400.0, 75.0)
        }
    }

    override fun onDock() {
        label.text = "Connected to ${databaseController.db}"
    }
}
