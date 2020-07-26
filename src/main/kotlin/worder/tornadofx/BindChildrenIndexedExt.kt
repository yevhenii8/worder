/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <BindChildrenIndexedExt.kt>
 * Created: <10/07/2020, 10:35:52 PM>
 * Modified: <26/07/2020, 05:22:51 PM>
 * Version: <2>
 */

package worder.tornadofx

import javafx.beans.WeakListener
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.FX
import tornadofx.getChildList
import java.lang.ref.WeakReference

/**
 * Bind the children of this Layout node to the given observable list of items by converting
 * them into nodes via the given converter function with provided indexes. Changes to the source list will be reflected
 * in the children list of this layout node.
 */
fun <T> EventTarget.bindChildrenIndexed(
        sourceList: ObservableList<T>,
        converter: (Int, T) -> Node
): ListConversionListener<T, Node> = requireNotNull(getChildList()?.bind(sourceList, converter)) {
    "Unable to extract child nodes from $this"
}


/**
 * Bind this list to the given observable list by converting them into the correct type via the given converter with indexes.
 * Changes to the observable list are synced.
 */
fun <SourceType, TargetType> MutableList<TargetType>.bind(
        sourceList: ObservableList<SourceType>,
        converter: (Int, SourceType) -> TargetType
): ListConversionListener<SourceType, TargetType> {
    val ignoringParentBuilder = FX::class.java.methods.find { it.name == "access\$setIgnoreParentBuilder\$cp" }!!
    val ignoringParentConverter: (Int, SourceType) -> TargetType = { index, source ->
        ignoringParentBuilder.invoke(FX, FX.IgnoreParentBuilder.Once)
        try {
            converter(index, source)
        } finally {
            ignoringParentBuilder.invoke(FX, FX.IgnoreParentBuilder.No)
        }
    }

    this.clear()
    this.addAll(sourceList.mapIndexed(ignoringParentConverter))

    val listener = ListConversionListener(this, ignoringParentConverter)
    sourceList.addListener(listener)

    return listener
}


/**
 * Listens to changes on a list of SourceType and keeps the target list in sync by converting
 * each object into the TargetType via the supplied converter with indexes.
 */
class ListConversionListener<SourceType, TargetType>(
        targetList: MutableList<TargetType>,
        private val converter: (Int, SourceType) -> TargetType
) : ListChangeListener<SourceType>, WeakListener {
    internal val targetRef: WeakReference<MutableList<TargetType>> = WeakReference(targetList)


    override fun onChanged(change: ListChangeListener.Change<out SourceType>) {
        val list = targetRef.get()

        if (list == null) {
            change.list.removeListener(this)
            return
        }

        while (change.next()) {
            when {
                change.wasPermutated() -> {
                    list.subList(change.from, change.to).clear()
                    list.addAll(change.from, change.list.subList(change.from, change.to).mapIndexed(converter))
                }
                change.wasRemoved() -> {
                    list.subList(change.from, change.from + change.removedSize).clear()
                }
                change.wasAdded() -> {
                    list.addAll(change.from, change.addedSubList.mapIndexed(converter))

                }
            }
        }
    }

    override fun wasGarbageCollected() = targetRef.get() == null

    override fun hashCode() = targetRef.get().hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true

        if (other is ListConversionListener<*, *>)
            return targetRef.get() == other.targetRef.get()

        return false
    }
}
