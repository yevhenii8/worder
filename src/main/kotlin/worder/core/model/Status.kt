package worder.core.model

import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.Label
import javafx.scene.paint.Paint
import tornadofx.attachTo
import tornadofx.onChange
import tornadofx.stringBinding
import tornadofx.tooltip

interface Status {
    val description: String
    val color: Paint
}

fun EventTarget.statusLabel(
        observableStatus: ObservableValue<out Status>,
        op: Label.() -> Unit = {}
) = Label().attachTo(this, op) { label ->
    label.textProperty().bind(observableStatus.stringBinding { it.toString() })

    fun updateStatusStyle() {
        label.tooltip(observableStatus.value.description)
        label.textFill = observableStatus.value.color
    }

    observableStatus.onChange { updateStatusStyle() }
    updateStatusStyle()
}
