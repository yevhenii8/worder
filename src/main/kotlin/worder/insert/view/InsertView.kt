package worder.insert.view

import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER
import javafx.stage.FileChooser.ExtensionFilter
import tornadofx.View
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.insets
import tornadofx.label
import tornadofx.replaceChildren
import tornadofx.scrollpane
import tornadofx.stackpane
import tornadofx.vbox
import worder.core.styles.WorderDefaultStyles
import worder.core.view.DragAndDropFragment
import worder.core.view.ObservableStatsFragment
import worder.core.view.statusLabel
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
    private val uncommittedUnitsUI: ScrollPane = getInsertUnitsContainer()
    private val committedUnitsUI: ScrollPane = getInsertUnitsContainer()
    private val insertModelUI = vbox()

    override val root: Parent = hbox(alignment = Pos.CENTER) {
        add(uncommittedUnitsUI)
        add(insertModelUI)
        add(committedUnitsUI)
    }


    override fun onDock() {
        val insertModel = insertController.currentInsertModel!!

        uncommittedUnitsUI.content.bindComponents(insertModel.uncommittedUnitsProperty) {
            find<InsertUnitFragment>("unit" to it)
        }

        committedUnitsUI.content.bindComponents(insertModel.committedUnitsProperty) {
            find<InsertUnitFragment>("unit" to it)
        }

        insertModelUI.apply {
            label("Insert Model")
            statusLabel(insertModel.modelStatusProperty)
            hbox {
                vbox {
                    padding = insets(top = 67, right = 10, bottom = 0, left = 10)
                    with (insertModel.observableStats) {
                        label(generatedUnitsProperty)
                        label(uncommittedUnitsProperty)
                        label(committedUnitsProperty)
                        label(excludedUnitsProperty)
                        label(actionNeededUnitsProperty)

                        label(totalValidWordsProperty)
                        label(totalInvalidWordsProperty)

                        label(totalProcessedProperty)
                        label(insertedProperty)
                        label(resetProperty)
                    }
                }
                add(find<ObservableStatsFragment>("observableStats" to insertModel.observableStats))
            }
        }
    }

    private fun getInsertUnitsContainer(): ScrollPane = scrollpane {
        vbarPolicy = NEVER
        hbarPolicy = NEVER

        add(vbox())
        addClass(WorderDefaultStyles.insertUnits)

        vvalueProperty().unbind()
        content.setOnScroll {
            vvalue -= it.deltaY * 0.01
        }
    }
}
