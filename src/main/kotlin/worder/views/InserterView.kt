package worder.views

import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollBar
import javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER
import javafx.stage.FileChooser.ExtensionFilter
import tornadofx.View
import tornadofx.addClass
import tornadofx.clear
import tornadofx.hbox
import tornadofx.insets
import tornadofx.replaceChildren
import tornadofx.scrollpane
import tornadofx.stackpane
import tornadofx.vbox
import worder.controllers.DatabaseChangeListener
import worder.controllers.DatabaseController
import worder.controllers.InserterController
import worder.views.fragments.DragAndDropFragment
import worder.views.fragments.InsertUnitFragment
import worder.views.fragments.NoConnectionFragment
import worder.views.styles.WorderStyle
import java.io.File

class InserterView : View("Inserter"), DatabaseChangeListener {
    private val inserterController: InserterController by inject()
    private val noConnectionFragment = find<NoConnectionFragment>().root


    init {
        find<DatabaseController>().subscribe(this)
    }


    override var root: Parent = stackpane {
        add(noConnectionFragment)
    }


    override fun onDatabaseConnection() {
        val fragment = find<DragAndDropFragment>(
                "text" to "Drag one or more plain files here to process them",
                "windowTitle" to "Inserter Files Selection",
                "extensionFilter" to ExtensionFilter("Text Files (*.txt)", "*.txt"),
                "action" to { files: List<File> -> inserterController.processFiles(files) }
        )

        root.replaceChildren(fragment.root)
    }

    override fun onDatabaseDisconnection() {
        root.replaceChildren(noConnectionFragment)
    }

    fun displayBatch() {
        root.replaceChildren(find<InserterProcessedView>().root)
    }
}

class InserterProcessedView : View() {
//    private lateinit var insertBatch: InsertBatch
    private var insertUnitFragments = vbox { }
    private val vScrollBar = ScrollBar().apply { orientation = VERTICAL }
    private val scrollPane = scrollpane {
        vbarPolicy = NEVER
        hbarPolicy = NEVER

        vScrollBar.minProperty().bind(vminProperty())
        vScrollBar.maxProperty().bind(vmaxProperty())
        vScrollBar.visibleAmountProperty().bind(heightProperty().divide(insertUnitFragments.heightProperty()))
        vvalueProperty().bindBidirectional(vScrollBar.valueProperty())

        add(insertUnitFragments)
        addClass(WorderStyle.insertUnits)

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
//        insertBatch = find<InserterController>().insertBatch
//
//        insertUnitFragments.clear()
//        insertBatch.units
//                .map { find<InsertUnitFragment>("insertUnit" to it) }
//                .forEach { insertUnitFragments.add(it) }
    }
}
