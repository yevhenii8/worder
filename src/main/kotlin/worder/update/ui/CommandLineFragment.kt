/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <CommandLineFragment.kt>
 * Created: <29/07/2020, 11:07:59 PM>
 * Modified: <31/07/2020, 10:53:55 PM>
 * Version: <67>
 */

package worder.update.ui

import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.Fragment
import tornadofx.box
import tornadofx.button
import tornadofx.hbox
import tornadofx.insets
import tornadofx.label
import tornadofx.px
import tornadofx.style
import tornadofx.textfield

class CommandLineFragment : Fragment() {
    val label: Label = label("DEF") {
        style {
            padding = box(0.px, 10.px)
            prefHeight = 40.px
            backgroundColor += Color.WHITE
            textFill = Color.DIMGRAY
            borderColor += box(Color.LIGHTGRAY)
            fontSize = 18.px
        }
    }

    val resetButton: Button = button("RESET") {
        style {
            prefHeight = 40.px
            borderColor += box(Color.LIGHTGRAY)
        }
    }

    val helpButton: Button = button("HELP") {
        style {
            prefHeight = 40.px
            borderColor += box(Color.LIGHTGRAY)
        }
    }

    val commitButton: Button = button("COMMIT") {
        style {
            prefHeight = 40.px
            borderColor += box(Color.LIGHTGRAY)
        }
    }

    val textField: TextField = textfield {
        promptText = "Command Line Interface"
        prefHeight = 40.0

        style {
            fontSize = 18.px
            borderColor += box(Color.LIGHTGRAY, Color.TRANSPARENT)
        }
    }


    override val root: HBox = hbox {
        add(label)
        add(textField)

        add(helpButton)
        add(resetButton)
        add(commitButton)

        HBox.setHgrow(textField, Priority.ALWAYS)
        HBox.setMargin(helpButton, insets(0, 0, 0, 25))
    }
}
