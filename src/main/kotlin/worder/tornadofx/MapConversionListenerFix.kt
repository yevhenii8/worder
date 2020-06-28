package worder.tornadofx

import javafx.beans.WeakListener
import javafx.collections.MapChangeListener
import javafx.collections.ObservableMap
import javafx.event.EventTarget
import javafx.scene.Node
import tornadofx.FX
import tornadofx.getChildList
import java.lang.ref.WeakReference

/*
Children's binding to map doesn't work due to https://github.com/edvin/tornadofx2/issues/8
So, had to put some workarounds instead
 */


/**
 * Bind the children of this Layout node to the given observable map of items
 * by converting them into nodes via the given converter function.
 * Changes to the source map will be reflected in the children list of this layout node.
 */
inline fun <reified K, reified V> EventTarget.bindChildren(
        sourceMap: ObservableMap<K, V>,
        noinline converter: (K, V) -> Node
): MapConversionListener<K, V, Node> = requireNotNull(
        getChildList()?.bind(sourceMap, converter)
) { "Unable to extract child nodes from $this" }


/**
 * Bind this map to the given observable list by converting them into the correct type via the given converter.
 * Changes to the observable list are synced.
 */
fun <SourceTypeKey, SourceTypeValue, TargetType> MutableList<TargetType>.bind(
        sourceMap: ObservableMap<SourceTypeKey, SourceTypeValue>,
        converter: (SourceTypeKey, SourceTypeValue) -> TargetType
): MapConversionListener<SourceTypeKey, SourceTypeValue, TargetType> {
    val ignoringParentBuilder = FX::class.java.methods.find { it.name == "access\$setIgnoreParentBuilder\$cp" }!!
    val ignoringParentConverter: (SourceTypeKey, SourceTypeValue) -> TargetType = { key, value ->
        ignoringParentBuilder.invoke(FX, FX.IgnoreParentBuilder.Once)
        try {
            converter(key, value)
        } finally {
            ignoringParentBuilder.invoke(FX, FX.IgnoreParentBuilder.No)
        }
    }

    val sourceMapListener = MapConversionListener(this, sourceMap, ignoringParentConverter)
    sourceMap.addListener(sourceMapListener)
    return sourceMapListener
}


/**
 * Listens to changes on a Map of SourceTypeKey to SourceTypeValue and keeps the target list synced by converting
 * each object into the TargetType via the supplied converter.
 */
class MapConversionListener<SourceTypeKey, SourceTypeValue, TargetType>(
        targetList: MutableList<TargetType>,
        sourceMap: ObservableMap<SourceTypeKey, SourceTypeValue>,
        val converter: (SourceTypeKey, SourceTypeValue) -> TargetType
) : MapChangeListener<SourceTypeKey, SourceTypeValue>, WeakListener {
    internal val targetRef: WeakReference<MutableList<TargetType>> = WeakReference(targetList)
    private val sourceToTarget: MutableMap<SourceTypeKey, TargetType> = sourceMap
            .map { it.key to converter.invoke(it.key, it.value) }.toMap(LinkedHashMap())


    init {
        targetList.clear()
        targetList.addAll(sourceToTarget.values)
    }


    override fun onChanged(change: MapChangeListener.Change<out SourceTypeKey, out SourceTypeValue>) {
        val list = targetRef.get()

        if (list == null) {
            change.map.removeListener(this)
            return
        }

        when {
            change.wasAdded() && change.wasRemoved() -> {
                val oldIndex = list.indexOf(sourceToTarget[change.key])
                sourceToTarget[change.key] = converter(change.key, change.valueAdded)
                list[oldIndex] = sourceToTarget[change.key]!!
            }
            change.wasRemoved() -> {
                list.remove(sourceToTarget[change.key])
                sourceToTarget.remove(change.key)
            }
            change.wasAdded() -> {
                sourceToTarget[change.key] = converter(change.key, change.valueAdded)
                list.add(sourceToTarget[change.key]!!)
            }
        }
    }

    override fun wasGarbageCollected() = targetRef.get() == null

    override fun hashCode() = targetRef.get().hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true

        if (other is MapConversionListener<*, *, *>)
            return targetRef.get() == other.targetRef.get()

        return false
    }
}