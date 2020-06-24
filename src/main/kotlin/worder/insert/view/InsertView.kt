package worder.insert.view

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER
import javafx.stage.FileChooser.ExtensionFilter
import tornadofx.View
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.replaceChildren
import tornadofx.scrollpane
import tornadofx.stackpane
import tornadofx.vbox
import worder.core.view.DragAndDropFragment
import worder.core.view.WorderBrightStyles
import worder.database.DatabaseController
import worder.database.DatabaseEventListener
import worder.database.model.WorderDB
import worder.database.view.NoConnectionFragment
import worder.insert.InsertController
import worder.tornadofx.bindComponents
import java.io.File

class InsertView : View("Insert"), DatabaseEventListener {
    private val insertUploadedView: InsertUploadedView by inject()
    private val insertController: InsertController by inject()
    private val notConnectedFragment = find<NoConnectionFragment>()
    private val notUploadedFragment = find<DragAndDropFragment>(
            "text" to "Drag one or more plain files here to process them",
            "windowTitle" to "Inserter Files Selection",
            "extensionFilter" to ExtensionFilter("Text Files (*.txt)", "*.txt"),
            "action" to { files: List<File> -> insertController.generateInsertModel(files) },
            "allowMultiselect" to true
    )

    override val root: Parent = stackpane()


    init {
        find<DatabaseController>().subscribeAndRaise(this)
    }


    override fun onDatabaseConnection(db: WorderDB) = toNotUploadedState()

    override fun onDatabaseDisconnection() = toNotConnectedState()

    fun toNotConnectedState() = root.replaceChildren(notConnectedFragment.root)

    fun toNotUploadedState() = root.replaceChildren(notUploadedFragment.root)

    fun toUploadedState() = root.replaceChildren(insertUploadedView.root)
}

class InsertUploadedView : View() {
    private val insertController: InsertController by inject()
    private val scrollPane = scrollpane {
        vbarPolicy = NEVER
        hbarPolicy = NEVER

        add(vbox())
        addClass(WorderBrightStyles.insertUnits)

        vvalueProperty().unbind()
        content.setOnScroll {
            vvalue -= it.deltaY * 0.01
        }
    }

    override val root: Parent = hbox(alignment = Pos.CENTER) {
        add(scrollPane)
    }


    override fun onDock() {
        val insertModel = insertController.currentInsertModel!!

        scrollPane.content.apply {
            bindComponents(insertModel.uncommittedUnitsProperty) {
                find<InsertUnitFragment>("unit" to it)
            }
        }
    }
}
