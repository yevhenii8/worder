package worder.core.view

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.Label
import tornadofx.attachTo
import tornadofx.onChange
import tornadofx.stringBinding
import tornadofx.tooltip
import worder.core.model.Status

inline fun EventTarget.worderStatusLabel(
        status: ObservableValue<out Status>,
        op: Label.() -> Unit = {}
) = Label().attachTo(this, op) { label ->
    label.textProperty().bind(status.stringBinding { "$it" })

    label.tooltip(status.value.description)
    label.textFill = status.value.color

    status.onChange {
        label.tooltip(status.value.description)
        label.textFill = status.value.color
    }
}
