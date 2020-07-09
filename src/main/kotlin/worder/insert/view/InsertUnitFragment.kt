/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <InsertUnitFragment.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <09/07/2020, 11:04:19 PM>
 * Version: <28>
 */

package worder.insert.view

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.bindChildren
import tornadofx.button
import tornadofx.enableWhen
import tornadofx.field
import tornadofx.fieldset
import tornadofx.fitToParentWidth
import tornadofx.fold
import tornadofx.form
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.imageview
import tornadofx.label
import tornadofx.onChange
import tornadofx.paddingTop
import tornadofx.removeWhen
import tornadofx.squeezebox
import tornadofx.textfield
import tornadofx.vbox
import worder.core.styles.WorderDefaultStyles
import worder.core.view.worderStatusLabel
import worder.insert.model.InsertUnit

class InsertUnitFragment : Fragment() {
    val unit: InsertUnit by param()


    override val root = vbox {
        addClass(WorderDefaultStyles.insertUnit)

        hbox(spacing = 20) {
            imageview("/icons/blue-document_64x64.png") {
                alignment = Pos.CENTER
            }

            vbox(alignment = Pos.BASELINE_RIGHT, spacing = 4) {
                hgrow = Priority.ALWAYS

                label("ID:")
                label("Status:")
                label("Source:")
                label("Valid Words:")
                label("Invalid Words:")
            }

            vbox(alignment = Pos.BASELINE_LEFT, spacing = 4) {
                hgrow = Priority.ALWAYS

                label(unit.id)
                worderStatusLabel(unit.statusProperty)
                label(unit.source)
                label(unit.validWordsProperty.sizeProperty())
                label(unit.invalidWordsProperty.sizeProperty())
            }

            vbox(spacing = 10) {
                button("Commit") {
                    enableWhen {
                        InsertUnit.InsertUnitAction.COMMIT.getListener()
                    }
                    setOnAction {
                        CoroutineScope(Dispatchers.Default).launch { unit.commit() }
                    }
                }

                button("Exclude") {
                    enableWhen {
                        InsertUnit.InsertUnitAction.EXCLUDE.getListener()
                    }
                    setOnAction {
                        unit.exclude()
                    }
                }

                button("Include") {
                    enableWhen {
                        InsertUnit.InsertUnitAction.INCLUDE.getListener()
                    }
                    setOnAction {
                        unit.include()
                    }
                }

                children.addClass(WorderDefaultStyles.unitButton)
            }
        }

        if (unit.invalidWordsProperty.isNotEmpty()) {
            squeezebox {
                paddingTop = 15
                removeWhen(unit.invalidWordsProperty.sizeProperty().isEqualTo(0))

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
        }
    }


    init {
        with(unit.statusProperty) {
            if (value == InsertUnit.InsertUnitStatus.COMMITTED)
                root.isDisable = true

            onChange { status ->
                if (status == InsertUnit.InsertUnitStatus.COMMITTED)
                    root.isDisable = true
            }
        }
    }


    private fun InsertUnit.InsertUnitAction.getListener(): ObservableValue<Boolean> {
        val listener = SimpleBooleanProperty(unit.statusProperty.value.availableActions.contains(this))
        unit.statusProperty.onChange {
            listener.value = it!!.availableActions.contains(this)
        }
        return listener
    }
}
