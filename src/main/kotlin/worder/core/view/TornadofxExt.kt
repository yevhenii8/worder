package worder.core.view

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.value.ObservableValue
import javafx.event.EventTarget
import javafx.scene.control.Label
import tornadofx.attachTo
import tornadofx.onChange
import tornadofx.stringBinding
import tornadofx.tooltip
import worder.core.model.Status

inline fun EventTarget.worderStatusLabel(
        observableStatus: ObservableValue<out Status>,
        op: Label.() -> Unit = {}
) = Label().attachTo(this, op) { label ->
    label.textProperty().bind(observableStatus.stringBinding { it.toString() })

    label.tooltip(observableStatus.value.description)
    label.textFill = observableStatus.value.color

    observableStatus.onChange {
        label.tooltip(observableStatus.value.description)
        label.textFill = observableStatus.value.color
    }
}

inline fun <reified T> EventTarget.worderPropertyLabel(
        property: ReadOnlyProperty<T>,
        op: Label.() -> Unit = {}
) = Label("${property.name}: ${property.value}").attachTo(this, op) { label ->
    label.textProperty().bind(property.stringBinding { "${property.name}: ${property.value}" })
}
