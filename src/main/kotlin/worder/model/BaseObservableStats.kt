package worder.model

import javafx.beans.property.MapProperty
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.SimpleMapProperty
import javafx.beans.value.ObservableValue
import tornadofx.getValue
import tornadofx.observable
import tornadofx.observableMapOf
import tornadofx.onChange
import kotlin.reflect.KProperty

open class BaseObservableStats(override val origin: String) : ObservableStats {
    companion object {
        fun statsObject(
                baseObservableStats: BaseObservableStats,
                initValue: Any?,
                defaultTitle: String? = null
        ): BaseObservableStats = baseObservableStats.bindToStats(initValue, defaultTitle)

        fun <T : Any?> statsObject(
                baseObservableStats: BaseObservableStats,
                observableValue: ObservableValue<T>,
                defaultTitle: String? = null
        ): BaseObservableStats = baseObservableStats.bindToStats(observableValue, defaultTitle)
    }


    private var initValueTmp: Any? = null
    private var observableValueTmp: ObservableValue<out Any?>? = null
    private var defaultTitleTmp: String? = null

    private val mutableTitleMapping: MutableMap<String, String> = mutableMapOf()
    private val mapProperty: MapProperty<String, Any?> = SimpleMapProperty(LinkedHashMap<String, Any?>().observable())
    private val map: MutableMap<String, Any?> by mapProperty
    private val titledMapProperty: MapProperty<String, Any?> = SimpleMapProperty(observableMapOf())
    private val titledMap: MutableMap<String, Any?> by titledMapProperty

    override val asMapProperty: ReadOnlyMapProperty<String, Any?> = mapProperty
    override val asMap: Map<String, Any?> = map
    override val asTitledMapProperty: ReadOnlyMapProperty<String, Any?> = titledMapProperty
    override val asTitledMap: Map<String, Any?> = titledMap
    override val titleMapping: Map<String, String> = mutableTitleMapping


    override fun updateTitle(propertyName: String, newTitle: String): Boolean {
        if (!mutableTitleMapping.containsKey(propertyName))
            return false

        val oldTitle: String = mutableTitleMapping.put(propertyName, newTitle)!!
        titledMap[newTitle] = titledMap[oldTitle]
        titledMap.remove(oldTitle)

        return true
    }

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any?> getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!map.containsKey(property.name))
            throw IllegalArgumentException("There's no value for property: ${property.name}")

        return map[property.name] as T
    }

    operator fun <T : Any?> setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (!map.containsKey(property.name))
            throw IllegalArgumentException("There's no value for property: ${property.name}")

        updatePropertyValue(property.name, value)
    }

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): BaseObservableStats {
        val title: String = defaultTitleTmp ?: property.name
        mutableTitleMapping[property.name] = title
        titledMap[title] = initValueTmp
        map[property.name] = initValueTmp

        observableValueTmp?.onChange { updatePropertyValue(property.name, it) }

        initValueTmp = null
        observableValueTmp = null
        defaultTitleTmp = null

        return this
    }


    protected fun bindToStats(initValue: Any?, defaultTitle: String? = null): BaseObservableStats {
        defaultTitleTmp = defaultTitle
        initValueTmp = initValue
        return this
    }

    protected fun <T : Any?> bindToStats(observableValue: ObservableValue<T>, defaultTitle: String? = null): BaseObservableStats {
        defaultTitleTmp = defaultTitle
        initValueTmp = observableValue.value
        observableValueTmp = observableValue
        return this
    }


    private fun updatePropertyValue(propertyName: String, value: Any?) {
        val title: String = mutableTitleMapping[propertyName]
                ?: throw IllegalStateException("There's no title mapping for property: $propertyName")

        map[propertyName] = value
        titledMap[title] = value
    }
}

inline fun <T : BaseObservableStats> T.applySynchronized(block: T.() -> Unit): T {
    synchronized(this) {
        block.invoke(this)
    }
    return this
}
