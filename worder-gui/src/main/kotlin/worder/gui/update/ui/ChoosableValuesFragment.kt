/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <ChoosableValuesFragment.kt>
 * Created: <29/07/2020, 07:38:48 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <64>
 */

package worder.gui.update.ui

import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.geometry.Orientation
import javafx.geometry.VPos
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.Separator
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import tornadofx.Fragment
import tornadofx.bind
import tornadofx.box
import tornadofx.constraintsForColumn
import tornadofx.enableWhen
import tornadofx.gridpane
import tornadofx.onChange
import tornadofx.px
import tornadofx.runLater
import tornadofx.sizeProperty
import tornadofx.style
import tornadofx.usePrefWidth
import tornadofx.warning

class ChoosableValuesFragment : Fragment() {
    private val choosableValues: ChoosableValues<Any?> by param()
    private val choosables: ObservableList<ChoosableValues<Any?>.ChoosableValue> = choosableValues.choosables
    private val chosenChoosables: ObservableList<ChoosableValues<Any?>.ChoosableValue> = choosableValues.chosenChoosables
    private var isNextCustomFieldFocused: Boolean = false


    init {
        choosables.apply {
            onChange { change: ListChangeListener.Change<out ChoosableValues<*>.ChoosableValue> ->
                if (change.next() && change.wasAdded()) {
                    root.apply {
                        children.removeIf {
                            GridPane.getRowIndex(it) == (rowCount - 1)
                        }

                        val initSize = size - change.addedSize
                        change.addedSubList.forEachIndexed { index, newChoosable ->
                            putChoosable(initSize - index, newChoosable)
                        }

                        appendCustomValueField()
                    }
                }
            }
        }
    }


    override val root: GridPane = gridpane {
        hgap = 5.0
        vgap = 5.0

        choosables.forEachIndexed { index, choosable ->
            putChoosable(index, choosable)
        }

        add(Separator(Orientation.VERTICAL), 1, 0, 1, GridPane.REMAINING)
        constraintsForColumn(0).minWidth = 40.0
        appendCustomValueField()
    }


    private fun GridPane.putChoosable(index: Int, choosable: ChoosableValues<*>.ChoosableValue) {
        Label().apply {
            bind(choosable.chosenOrdinalProperty)
            add(this, 0, index, 1, 1)
            GridPane.setValignment(this, VPos.TOP)
        }

        val checkBox = CheckBox().apply {
            selectedProperty().bindBidirectional(choosable.isChosenProperty)
            enableWhen(chosenChoosables.sizeProperty.lessThan(choosableValues.chooseLimit).or(selectedProperty()))
            add(this, 2, index, 1, 1)
            GridPane.setValignment(this, VPos.TOP)
        }

        Label("${index + 1})").apply {
            usePrefWidth = true
            disableProperty().bind(checkBox.disableProperty())
            add(this, 3, index, 1, 1)
            GridPane.setValignment(this, VPos.TOP)
        }

        Label(choosable.value.toString()).apply {
            isWrapText = true
            disableProperty().bind(checkBox.disableProperty())
            add(this, 4, index, 1, 1)
        }
    }

    private fun GridPane.appendCustomValueField() {
        val newRowIndex = rowCount
        val textField = TextField()

        val button = Button("+").apply {
            style {
                padding = box(3.px)
            }

            setOnAction {
                val input = textField.text.also {
                    if (it.isBlank()) {
                        warning("Please type something in the field!")
                        return@setOnAction
                    }
                }

                isNextCustomFieldFocused = true
                val newChoosable = choosableValues.proposeNewValue(input)?.let {
                    it.isChosen = true
                }

                if (newChoosable == null) {
                    isNextCustomFieldFocused = false
                    warning("You can't add duplicated value!")
                }

                textField.text = null
            }

            enableWhen(chosenChoosables.sizeProperty.lessThan(choosableValues.chooseLimit))
            add(this, 2, newRowIndex, 1, 1)
        }

        Label("${newRowIndex + 1})").apply {
            usePrefWidth = true
            add(this, 3, newRowIndex, 1, 1)
            disableProperty().bind(button.disableProperty())
        }

        textField.apply {
            promptText = "custom value"

            style {
                backgroundColor += Color.TRANSPARENT
                padding = box(0.px)
            }

            setOnKeyPressed {
                if (it.code == KeyCode.ENTER) {
                    button.onAction.handle(null)
                }
            }

            disableProperty().bind(button.disableProperty())
            add(this, 4, newRowIndex, 1, 1)

            if (isNextCustomFieldFocused) {
                isNextCustomFieldFocused = false
                runLater { textField.requestFocus() }
            }
        }
    }
}