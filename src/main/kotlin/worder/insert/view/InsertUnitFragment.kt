package worder.insert.view

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.bindChildren
import tornadofx.button
import tornadofx.field
import tornadofx.fieldset
import tornadofx.fold
import tornadofx.form
import tornadofx.getChildList
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.insets
import tornadofx.label
import tornadofx.onChange
import tornadofx.squeezebox
import tornadofx.textfield
import tornadofx.vbox
import worder.insert.model.InsertUnit
import worder.core.view.WorderBrightStyle

class InsertUnitFragment : Fragment() {
    private val unit: InsertUnit by param()

    override val root = vbox {
        hbox(spacing = 20) {
            imageview("/icons/blue-document-icon_64x64.png") {
                HBox.setMargin(this, insets(right = 15))
                alignment = Pos.CENTER
            }

            vbox(alignment = Pos.BASELINE_RIGHT) {
                label("ID:")
                label("Status:")
                label("Source:")
                label("Valid Words:")
                label("Invalid Words:")
            }

            vbox(alignment = Pos.BASELINE_LEFT) {
                label(unit.idProperty)

//                label(unit.statusProperty) {
//                    fun updateStatusStyling() {
//                        tooltip(unit.status.description)
//                        textFill = when (unit.status) {
//                            InsertUnit.InsertUnitStatus.READY_TO_COMMIT -> Color.GREEN
//                            InsertUnit.InsertUnitStatus.ACTION_NEEDED -> Color.RED
//                            InsertUnit.InsertUnitStatus.EXCLUDED_FROM_COMMIT -> Color.YELLOW
//                            InsertUnit.InsertUnitStatus.COMMITTING -> Color.GREEN
//                            InsertUnit.InsertUnitStatus.COMMITTED -> Color.GREEN
//                        }
//                    }
//
//                    unit.statusProperty.onChange { updateStatusStyling() }
//                    updateStatusStyling()
//                }

                label(unit.sourceProperty)
                label(unit.validWordsProperty.sizeProperty())
                label(unit.invalidWordsProperty.sizeProperty())
            }

            vbox(spacing = 10) {
                button("Commit")
                button("Exclude")
                button("Include")

                addClass(WorderBrightStyle.unitActions)
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
                                                tornadofx.error("Validation error", "Please, type valid word!")
                                        }
                                    }

                                    button("x") {
                                        setOnAction {
                                            invalidWord.reject()
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

        addClass(WorderBrightStyle.insertUnit)
    }
}
