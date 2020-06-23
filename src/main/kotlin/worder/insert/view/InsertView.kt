package worder.insert.view

import javafx.geometry.Orientation.VERTICAL
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollBar
import javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER
import javafx.scene.layout.VBox
import javafx.stage.FileChooser.ExtensionFilter
import tornadofx.View
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.insets
import tornadofx.onChange
import tornadofx.replaceChildren
import tornadofx.scrollpane
import tornadofx.stackpane
import tornadofx.vbox
import worder.core.view.DragAndDropFragment
import worder.core.view.WorderBrightStyles
import worder.database.view.NoConnectionFragment
import worder.insert.InsertController
import worder.tornadofx.bindComponents
import java.io.File

class InsertView : View("Insert") {
    private val insertUploadedView: InsertUploadedView by inject()
    private val insertController: InsertController by inject()
    private val notConnectedFragment = find<NoConnectionFragment>()
    private val notUploadedFragment = find<DragAndDropFragment>(
            "text" to "Drag one or more plain files here to process them",
            "windowTitle" to "Inserter Files Selection",
            "extensionFilter" to ExtensionFilter("Text Files (*.txt)", "*.txt"),
            "action" to { files: List<File> -> insertController.generateInsertModel(files) }
    )


    override val root: Parent = stackpane()


    fun toNotConnectedState() = root.replaceChildren(notConnectedFragment.root)

    fun toNotUploadedState() = root.replaceChildren(notUploadedFragment.root)

    fun toUploadedState() = root.replaceChildren(insertUploadedView.root)
}

class InsertUploadedView : View() {
    private val insertController: InsertController by inject()
    private val vScrollBar = ScrollBar().apply { orientation = VERTICAL }
    private val scrollPane = scrollpane {
        vbarPolicy = NEVER
        hbarPolicy = NEVER

        add(vbox())
        addClass(WorderBrightStyles.insertUnits)

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

        vbox {
            TODO("View of Insert Model itself :D ")
        }
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
