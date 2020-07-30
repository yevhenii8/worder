/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <SetBindIndexed.kt>
 * Created: <30/07/2020, 10:49:54 PM>
 * Modified: <30/07/2020, 11:18:53 PM>
 * Version: <4>
 */

package worder.tornadofx

import javafx.beans.WeakListener
import javafx.beans.binding.IntegerBinding
import javafx.collections.ObservableSet
import javafx.collections.SetChangeListener
import tornadofx.FX
import tornadofx.integerBinding
import java.lang.ref.WeakReference

@Suppress("DuplicatedCode")
fun <SourceType, TargetType> MutableSet<TargetType>.bindIndexed(
        sourceSet: ObservableSet<SourceType>,
        converter: (Int, SourceType) -> TargetType
): IndexedSetConversionListener<SourceType, TargetType> {
    val ignoringParentBuilder = FX::class.java.methods.find { it.name == "access\$setIgnoreParentBuilder\$cp" }!!
    val ignoringParentConverter: (Int, SourceType) -> TargetType = { index, value ->
        ignoringParentBuilder.invoke(FX, FX.IgnoreParentBuilder.Once)
        try {
            converter(index, value)
        } finally {
            ignoringParentBuilder.invoke(FX, FX.IgnoreParentBuilder.No)
        }
    }

    val listener = IndexedSetConversionListener(sourceSet, this, ignoringParentConverter)
    sourceSet.addListener(listener)

    return listener
}

class IndexedSetConversionListener<SourceType, TargetType>(
        sourceSet: Set<SourceType>,
        targetSet: MutableSet<TargetType>,
        private val converter: (Int, SourceType) -> TargetType
) : SetChangeListener<SourceType>, WeakListener {
    private val targetRef: WeakReference<MutableSet<TargetType>> = WeakReference(targetSet)
    private val sourceToTarget: MutableMap<SourceType, TargetType> = sourceSet
            .mapIndexed { index, value -> value to converter(index, value) }
            .toMap(LinkedHashMap())


    init {
        targetSet.clear()
        targetSet.addAll(sourceToTarget.values)
    }


    override fun onChanged(change: SetChangeListener.Change<out SourceType>) {
        val set = targetRef.get()
        val originalSet = change.set

        if (set == null) {
            originalSet.removeListener(this)
            return
        }

        when {
            change.wasRemoved() -> {
                set.remove(sourceToTarget[change.elementRemoved])
            }
            change.wasAdded() -> {
                val newValue = converter(originalSet.indexOf(change.elementAdded), change.elementAdded)
                sourceToTarget[change.elementAdded] = newValue
                set.add(newValue)
            }
        }
    }

    override fun wasGarbageCollected() = targetRef.get() == null

    override fun hashCode() = targetRef.get().hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true

        if (other is IndexedSetConversionListener<*, *>)
            return targetRef.get() == other.targetRef.get()

        return false
    }
}

fun <T> ObservableSet<T>.onChange(op: (SetChangeListener.Change<out T>) -> Unit) = apply {
    addListener(op)
}

val ObservableSet<*>.sizeProperty: IntegerBinding
    get() = integerBinding(this) { size }
