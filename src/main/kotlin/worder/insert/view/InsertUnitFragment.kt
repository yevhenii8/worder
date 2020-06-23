package worder.insert.view

import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import kotlinx.coroutines.runBlocking
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.bindChildren
import tornadofx.button
import tornadofx.field
import tornadofx.fieldset
import tornadofx.fitToParentWidth
import tornadofx.fold
import tornadofx.form
import tornadofx.getChildList
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.imageview
import tornadofx.label
import tornadofx.onChange
import tornadofx.squeezebox
import tornadofx.textfield
import tornadofx.tooltip
import tornadofx.useMaxSize
import tornadofx.vbox
import worder.core.model.statusLabel
import worder.core.view.WorderBrightStyles
import worder.insert.model.InsertUnit

class InsertUnitFragment : Fragment() {
    private val unit: InsertUnit by param()

    override val root = vbox {
        hbox(spacing = 20) {
            imageview("/icons/blue-document-icon_64x64.png") {
                alignment = Pos.CENTER
            }

            vbox(alignment = Pos.BASELINE_RIGHT, spacing = 5) {
                hgrow = Priority.ALWAYS

                label("ID:")
                label("Status:")
                label("Source:")
                label("Valid Words:")
                label("Invalid Words:")
            }

            vbox(alignment = Pos.BASELINE_LEFT, spacing = 5) {
                hgrow = Priority.ALWAYS

                label(unit.idProperty)
                statusLabel(unit.unitStatusProperty)
//                label(unit.unitStatusProperty) {
//                    fun updateStatusStyling() {
//                        tooltip(unit.unitStatus.description)
//                        textFill = when (unit.unitStatus) {
//                            InsertUnit.InsertUnitStatus.READY_TO_COMMIT -> Color.GREEN
//                            InsertUnit.InsertUnitStatus.ACTION_NEEDED -> Color.RED
//                            InsertUnit.InsertUnitStatus.EXCLUDED_FROM_COMMIT -> Color.ORANGE
//                            InsertUnit.InsertUnitStatus.COMMITTING -> Color.GREEN
//                            InsertUnit.InsertUnitStatus.COMMITTED -> Color.GREEN
//                        }
//                    }
//
//                    unit.unitStatusProperty.onChange { updateStatusStyling() }
//                    updateStatusStyling()
//                }
                label(unit.sourceProperty)
                label(unit.validWordsProperty.sizeProperty())
                label(unit.invalidWordsProperty.sizeProperty())
            }

            vbox(spacing = 10) {
                button("Commit") {
                    setOnAction {
                        runBlocking { unit.commit() }
                    }
                }
                button("Exclude") {
                    setOnAction {
                        runBlocking { unit.excludeFromCommit() }
                    }
                }
                button("Include") {
                    setOnAction {
                        runBlocking { unit.includeInCommit() }
                    }
                }

                addClass(WorderBrightStyles.unitButtons)
            }
        }

        if (unit.invalidWords.isNotEmpty()) {
            val squeezebox = squeezebox {
                fold("List of invalid words") {
                    form {
                        fieldset {
                            bindChildren(unit.invalidWordsProperty) { invalidWord ->
                                field(invalidWord.value) {
                                    val textFiled = textfield(invalidWord.value)

                                    button("OK") {
                                        setOnAction {
                                            if (!invalidWord.substitute(textFiled.text))
                                                tornadofx.error("Validation error", "Please, type a valid word!")
                                            form.fitToParentWidth()
                                        }
                                    }

                                    button("x") {
                                        setOnAction {
                                            invalidWord.reject()
                                            form.fitToParentWidth()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            unit.invalidWordsProperty.sizeProperty().onChange {
                if (it == 0)
                    getChildList()?.remove(squeezebox)
            }
        }

        addClass(WorderBrightStyles.insertUnit)
    }
}
