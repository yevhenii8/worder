package worder.tornadofx

import javafx.collections.ObservableSet
import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.SetConversionListener
import tornadofx.UIComponent
import tornadofx.bind
import tornadofx.getChildList

/*
There's no extension function for bindComponents(ObservableList): [ISSUE IS NEEDED]
So, had to put some workarounds instead
 */


/**
 * Bind the children of this Layout node to the given observable set of items by converting
 * them into UIComponents via the given converter function. Changes to the source set will be reflected
 * in the children list of this layout node.
 */
inline fun <reified T> EventTarget.bindComponents(
        sourceSet: ObservableSet<T>,
        noinline converter: (T) -> UIComponent
): SetConversionListener<T, Node> = requireNotNull(getChildList()?.bind(sourceSet) { converter(it).root }) {
    "Unable to extract child nodes from $this"
}