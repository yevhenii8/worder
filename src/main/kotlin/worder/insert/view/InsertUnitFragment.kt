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
import tornadofx.getChildList
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.imageview
import tornadofx.label
import tornadofx.onChange
import tornadofx.squeezebox
import tornadofx.textfield
import tornadofx.vbox
import worder.core.styles.WorderDefaultStyles
import worder.core.view.worderStatusLabel
import worder.insert.model.InsertUnit

class InsertUnitFragment : Fragment() {
    private val unit: InsertUnit by param()

    override val root = vbox {
        hbox(spacing = 20) {
            imageview("/icons/blue-document-icon_64x64.png") {
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
                fun InsertUnit.InsertUnitAction.getListener(): ObservableValue<Boolean> {
                    val listener = SimpleBooleanProperty(unit.statusProperty.value.availableActions.contains(this))
                    unit.statusProperty.onChange {
                        listener.value = it!!.availableActions.contains(this)
                    }
                    return listener
                }

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

                addClass(WorderDefaultStyles.unitButtons)
            }
        }

        if (unit.invalidWordsProperty.isNotEmpty()) {
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

        addClass(WorderDefaultStyles.insertUnit)
    }
}
