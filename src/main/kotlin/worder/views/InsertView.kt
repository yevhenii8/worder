package worder.views

import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollBar
import javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER
import javafx.scene.layout.VBox
import javafx.stage.FileChooser.ExtensionFilter
import tornadofx.View
import tornadofx.addClass
import tornadofx.bindComponents
import tornadofx.hbox
import tornadofx.insets
import tornadofx.replaceChildren
import tornadofx.scrollpane
import tornadofx.stackpane
import tornadofx.vbox
import worder.controllers.DatabaseController
import worder.controllers.DatabaseEventListener
import worder.controllers.InsertController
import worder.model.database.WorderDB
import worder.model.insert.InsertModel
import worder.views.fragments.DragAndDropFragment
import worder.views.fragments.InsertUnitFragment
import worder.views.fragments.NoConnectionFragment
import worder.views.styles.WorderStyle
import java.io.File

class InsertView : View("Insert"), DatabaseEventListener {
    private val insertController: InsertController by inject()
    private val noConnectionFragment = find<NoConnectionFragment>().root

    override val root: Parent = stackpane()


    init {
        find<DatabaseController>().subscribeAndRaise(this)
    }


    override fun onDatabaseConnection(db: WorderDB) {
        val fragment = find<DragAndDropFragment>(
                "text" to "Drag one or more plain files here to process them",
                "windowTitle" to "Inserter Files Selection",
                "extensionFilter" to ExtensionFilter("Text Files (*.txt)", "*.txt"),
                "action" to { files: List<File> -> insertController.uploadFiles(files) }
        )

        root.replaceChildren(fragment.root)
    }

    override fun onDatabaseDisconnection() {
        root.replaceChildren(noConnectionFragment)
    }

    fun showUploadedView() {
        root.replaceChildren(find<InserterUploadedView>().root)
    }
}

class InserterUploadedView : View() {
    private lateinit var insertModel: InsertModel
    private val insertController: InsertController by inject()
    private val vScrollBar = ScrollBar().apply { orientation = VERTICAL }
    private val scrollPane = scrollpane {
        vbarPolicy = NEVER
        hbarPolicy = NEVER

        add(vbox())
        addClass(WorderStyle.insertUnits)

        vScrollBar.minProperty().bind(vminProperty())
        vScrollBar.maxProperty().bind(vmaxProperty())
        vScrollBar.visibleAmountProperty().bind(heightProperty().divide((content as VBox).heightProperty()))
        vvalueProperty().bindBidirectional(vScrollBar.valueProperty())

        content.setOnScroll {
            vvalue -= it.deltaY * 0.01
        }
    }

    override val root: Parent = hbox(alignment = Pos.CENTER) {
        hbox {
            padding = insets(0, 25)
            add(vScrollBar)
            add(scrollPane)
        }
    }


    override fun onDock() {
        insertModel = insertController.insertModel!!

        scrollPane.content.apply {
            bindComponents(insertModel.uncommittedUnitsProperty) {
                find<InsertUnitFragment>("unit" to it)
            }
        }
    }
}
