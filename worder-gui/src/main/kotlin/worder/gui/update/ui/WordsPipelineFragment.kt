/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WordsPipelineFragment.kt>
 * Created: <20/07/2020, 06:26:55 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <240>
 */

package worder.gui.update.ui

import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ScrollBar
import javafx.scene.control.ScrollPane
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import tornadofx.Fragment
import tornadofx.bindChildren
import tornadofx.borderpane
import tornadofx.box
import tornadofx.fitToParentSize
import tornadofx.getChildList
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.onChange
import tornadofx.paddingHorizontal
import tornadofx.progressindicator
import tornadofx.px
import tornadofx.runLater
import tornadofx.scrollpane
import tornadofx.style
import tornadofx.vbox
import tornadofx.visibleWhen
import worder.gui.core.observableStats
import worder.gui.database.DatabaseController
import worder.gui.update.UpdateTabView
import worder.gui.update.model.WordsPipeline

class WordsPipelineFragment : Fragment() {
    private val wordsPipeline: WordsPipeline by param()
    private val commandLineUI: CommandLineFragment = find()
    private lateinit var scrollPaneUI: ScrollPane


    override val root: BorderPane = borderpane {
        center = vbox(spacing = 20) {
            hbox {
                fitToParentSize()

                val scrollBarUI: ScrollBar = ScrollBar().apply {
                    orientation = Orientation.VERTICAL
                    this@hbox.add(this)
                }

                scrollPaneUI = scrollpane {
                    fitToParentSize()
                    vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
                    isFitToWidth = true
                    paddingHorizontal = 20

                    content = vbox(spacing = 20, alignment = Pos.BOTTOM_CENTER) {
                        minHeightProperty().bind(this@scrollpane.heightProperty())
                        heightProperty().onChange { vvalue = 1.0 }
                        bindChildren(wordsPipeline.pipelineProperty) {
                            find<WordBlockFragment>("block" to it, "clFragment" to commandLineUI).root
                        }
                    }

                    scrollBarUI.apply {
                        minProperty().bind(vminProperty())
                        maxProperty().bind(vmaxProperty())
                        visibleAmountProperty().bind(heightProperty().divide((content as VBox).heightProperty()))
                        vvalueProperty().bindBidirectional(valueProperty())
                    }
                }
            }
            add(commandLineUI)
        }

        right = vbox(spacing = 20, alignment = Pos.CENTER) {
            paddingHorizontal = 125
            wordsPipeline.usedRequesters.forEach {
                observableStats(observableStats = it.observableStats, hideNullable = true) {
                    (children[0] as Label).graphic = progressindicator {
                        setPrefSize(20.0, 20.0)
                        visibleWhen(it.isBusyProperty)
                    }
                }
            }
        }
    }


    init {
        fun disableCommandLineUI() {
            commandLineUI.apply {
                label.isDisable = true
                textField.isDisable = true
                commitButton.isDisable = true
            }
        }

        commandLineUI.apply {
            commitButton.setOnAction {
                find<UpdateTabView>().commitLast()
            }

            resetButton.setOnAction {
                find<DatabaseController>().db?.let { database ->
                    find<UpdateTabView>().onDatabaseConnection(database)
                }
            }

            runLater { textField.requestFocus() }
        }

        wordsPipeline.isConsumedProperty.onChange {
            if (it == true) {
                val conveyor = scrollPaneUI.content.getChildList()!!

                if (conveyor.isEmpty()) {
                    disableCommandLineUI()
                    scrollPaneUI.apply {
                        isFitToHeight = true
                        content = find<EmptyPipeline>().root
                    }
                    return@onChange
                }

                conveyor.add(
                        label("ALL THE WORDS HAVE BEEN UPDATED").apply {
                            style {
                                fontSize = 18.px
                                padding = box(0.px, 0.px, 20.px, 0.px)
                            }
                        }
                )
            }
        }

        wordsPipeline.isCommittedProperty.onChange {
            if (it == true) {
                disableCommandLineUI()
            }
        }
    }


    class EmptyPipeline : Fragment() {
        override val root: Parent = vbox(spacing = 15, alignment = Pos.CENTER) {
            imageview("/images/empty-box.png")
            label("NO WORDS TO UPDATE").style { fontSize = 18.px }
        }
    }
}
