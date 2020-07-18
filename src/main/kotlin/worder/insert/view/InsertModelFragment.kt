/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <InsertModelFragment.kt>
 * Created: <09/07/2020, 10:43:11 PM>
 * Modified: <19/07/2020, 12:00:08 AM>
 * Version: <171>
 */

package worder.insert.view

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.HPos
import javafx.geometry.HorizontalDirection
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Parent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.pdfsam.ui.RingProgressIndicator
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.box
import tornadofx.button
import tornadofx.confirm
import tornadofx.gridpane
import tornadofx.gridpaneConstraints
import tornadofx.label
import tornadofx.observableListOf
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.px
import tornadofx.row
import tornadofx.separator
import tornadofx.style
import tornadofx.vbox
import worder.core.formatGrouped
import worder.core.styles.WorderDefaultStyles
import worder.core.view.listBasedStats
import worder.core.view.worderStatusLabel
import worder.insert.InsertController
import worder.insert.model.InsertModel
import worder.insert.model.InsertUnit
import kotlin.math.roundToInt

class InsertModelFragment : Fragment() {
    private val insertModel: InsertModel by param()
    private val uncommittedUnits: ObservableList<InsertUnit> = insertModel.run {
        val res = observableListOf(actionNeededUnits + readyToCommitUnits)

        committedUnitsProperty.onChange { op: ListChangeListener.Change<out InsertUnit> ->
            if (op.next() && op.wasAdded())
                res.removeAll(op.addedSubList)
        }

        res
    }
    private val progressIndicator = RingProgressIndicator().apply {
        insertModel.observableStats.totalProcessedProperty.onChange {
            this.progress = ((it.toDouble() / insertModel.observableStats.totalWords) * 100).roundToInt()
        }
    }
    private val uncommittedUnitsUI = find<InsertUnitsContainerFragment>(
            "units" to uncommittedUnits,
            "direction" to HorizontalDirection.LEFT
    ).root
    private val committedUnitsUI = find<InsertUnitsContainerFragment>(
            "units" to insertModel.committedUnitsProperty,
            "direction" to HorizontalDirection.RIGHT
    ).root


    override val root: Parent = borderpane {
        paddingAll = 15


        left = vbox(spacing = 20, alignment = Pos.TOP_CENTER) {
            label("UNCOMMITTED UNITS").style { fontSize = 20.px }
            separator()
            add(uncommittedUnitsUI)
        }

        center = vbox(spacing = 20, alignment = Pos.TOP_CENTER) {
            BorderPane.setMargin(this, Insets(0.0, 10.0, 0.0, 10.0))
            addClass(WorderDefaultStyles.insertModel)

            label("INSERT MODEL")
            separator()
            gridpane {
                alignment = Pos.CENTER
                hgap = 30.0
                vgap = 60.0


                row {
                    val insertProgressStats = listBasedStats(
                            statsProperties = listOf(
                                    insertModel.observableStats.generatedUnitsProperty,
                                    insertModel.observableStats.readyToCommitUnitsProperty,
                                    insertModel.observableStats.actionNeededUnitsProperty,
                                    insertModel.observableStats.excludedUnitsProperty,
                                    insertModel.observableStats.committedUnitsProperty
                            ),
                            commonValueMutator = { (this as Int).formatGrouped() }
                    ).root.apply {
                        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
                        style {
                            borderColor += box(Color.TRANSPARENT)
                        }
                    }

                    add(insertProgressStats)
                    vbox(spacing = 10, alignment = Pos.BASELINE_CENTER) {
                        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)
                        add(progressIndicator)
                        worderStatusLabel(insertModel.modelStatusProperty)
                    }
                }
                row {
                    separator {
                        gridpaneConstraints {
                            columnSpan = 2
                        }
                    }
                }
                row {
                    add(listBasedStats(
                            blockTitle = "Uploaded Data Stats",
                            statsProperties = listOf(
                                    insertModel.observableStats.totalWordsProperty,
                                    insertModel.observableStats.totalValidWordsProperty,
                                    insertModel.observableStats.totalInvalidWordsProperty
                            ),
                            commonValueMutator = { (this as Int).formatGrouped() }
                    ))
                    add(listBasedStats(
                            blockTitle = "Processed Data Stats",
                            statsProperties = listOf(
                                    insertModel.observableStats.totalProcessedProperty,
                                    insertModel.observableStats.insertedProperty,
                                    insertModel.observableStats.resetProperty
                            ),
                            commonValueMutator = { (this as Int).formatGrouped() }
                    ))
                }
                row {
                    separator {
                        gridpaneConstraints {
                            columnSpan = 2
                        }
                    }
                }
                row {
                    button("Reset This Model") {
                        setOnAction {
                            if (uncommittedUnits.isNotEmpty())
                                confirm("There are uncommitted units here. Are you sure you want to reset this model ?") {
                                    find<InsertController>().releaseInsertModel()
                                }
                            else
                                find<InsertController>().releaseInsertModel()
                        }
                    }
                    button("Commit All Units") {
                        setOnAction {
                            CoroutineScope(Dispatchers.Default).launch {
                                insertModel.commitAllUnits()
                            }
                        }
                    }
                }

                children.forEach {
                    it.gridpaneConstraints {
                        this.hAlignment = HPos.CENTER
                        this.vAlignment = VPos.CENTER
                    }
                }
            }
        }

        right = vbox(spacing = 20, alignment = Pos.TOP_CENTER) {
            label("COMMITTED UNITS").style { fontSize = 20.px }
            separator()
            add(committedUnitsUI)
        }


        (left as VBox).minWidthProperty().bind(committedUnitsUI.widthProperty())
        (right as VBox).minWidthProperty().bind(uncommittedUnitsUI.widthProperty())
    }
}
