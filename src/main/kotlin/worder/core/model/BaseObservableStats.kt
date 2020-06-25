package worder.core.model

import javafx.beans.property.MapProperty
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.SimpleMapProperty
import javafx.beans.value.ObservableValue
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tornadofx.getValue
import tornadofx.onChange
import worder.tornadofx.observableMapOf
import kotlin.reflect.KProperty

open class BaseObservableStats(override val origin: String) : ObservableStats {
    companion object {
        /**
         * Analogues of BaseObservableStats.bindToStats(...) methods.
         */

        fun statsObject(
                baseObservableStats: BaseObservableStats,
                initValue: Any?,
                defaultTitle: String? = null
        ): BaseObservableStats = baseObservableStats.bindToStats(initValue, defaultTitle)

        fun <T : Any?> statsObject(
                baseObservableStats: BaseObservableStats,
                source: ObservableValue<T>,
                defaultTitle: String? = null
        ): BaseObservableStats = baseObservableStats.bindToStats(source, defaultTitle)

        fun <T : Any?> statsObject(
                baseObservableStats: BaseObservableStats,
                source: ReadOnlyProperty<T>,
                defaultTitle: String? = null,
                usePropertyNameAsTitle: Boolean = true
        ): BaseObservableStats = baseObservableStats.bindToStats(source, defaultTitle, usePropertyNameAsTitle)
    }


    private var initValueTmp: Any? = null
    private var observableValueTmp: ObservableValue<out Any?>? = null
    private var defaultTitleTmp: String? = null

    private val mutableTitleMapping: MutableMap<String, String> = mutableMapOf()
    private val mapProperty: MapProperty<String, Any?> = SimpleMapProperty(observableMapOf())
    private val map: MutableMap<String, Any?> by mapProperty
    private val titledMapProperty: MapProperty<String, Any?> = SimpleMapProperty(observableMapOf())
    private val titledMap: MutableMap<String, Any?> by titledMapProperty

    override val asMapProperty: ReadOnlyMapProperty<String, Any?> = mapProperty
    override val asMap: Map<String, Any?> = map
    override val asTitledMapProperty: ReadOnlyMapProperty<String, Any?> = titledMapProperty
    override val asTitledMap: Map<String, Any?> = titledMap
    override val titleMapping: Map<String, String> = mutableTitleMapping


    /**
     * These methods provide Kotlin properties delegation.
     * They ARE NOT meant to be used by end users.
     */

    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any?> getValue(thisRef: Any?, property: KProperty<*>): T {
        require(map.containsKey(property.name)) {
            "There's no such property under this stats object! trying to get: ${property.name}"
        }

        return map[property.name] as T
    }

    operator fun <T : Any?> setValue(thisRef: Any?, property: KProperty<*>, value: T) = updatePropertyValue(property.name, value)

    operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): BaseObservableStats {
        val title: String = defaultTitleTmp ?: mutableTitleMapping[property.name] ?: property.name
        val oldMapping = mutableTitleMapping.entries.find { it.value == title }?.key

        // handling properties overriding here
        require(oldMapping == null || property.name == oldMapping) {
            "Can't bind new property! Title [$title] is already presented! " +
                    "Current bound: [$title -> ${mutableTitleMapping.entries.find { it.value == title }?.key}] " +
                    "Proposed bound: [$title -> ${property.name}]"
        }

        mutableTitleMapping[property.name] = title
        titledMap[title] = initValueTmp
        map[property.name] = initValueTmp

        observableValueTmp?.onChange {
            updatePropertyValue(property.name, it)
        }

        initValueTmp = null
        observableValueTmp = null
        defaultTitleTmp = null

        return this
    }


    /**
     * Methods for delegate instantiating. They are meant to be used by end users in order to fill stats object with values.
     * They should be used to pass property's work to Stats Object and therefore make it visible for users of this stats object.
     */

    protected fun bindToStats(
            initValue: Any?,
            defaultTitle: String? = null
    ): BaseObservableStats {
        defaultTitleTmp = defaultTitle
        initValueTmp = initValue
        return this
    }

    protected fun <T : Any?> bindToStats(
            source: ObservableValue<T>,
            defaultTitle: String? = null
    ): BaseObservableStats {
        observableValueTmp = source
        return bindToStats(source.value, defaultTitle)
    }

    protected fun <T : Any?> bindToStats(
            source: ReadOnlyProperty<T>,
            defaultTitle: String? = null,
            usePropertyNameAsTitle: Boolean = true
    ): BaseObservableStats {
        val title = defaultTitle ?: if (usePropertyNameAsTitle && source.name.isNotBlank()) source.name else null
        return bindToStats(source as ObservableValue<T>, title)
    }


    /**
     * Allows users to change Title of any Property in runtime.
     */

    override fun updateTitle(propertyName: String, newTitle: String): Boolean {
        if (!mutableTitleMapping.containsKey(propertyName))
            return false

        val oldTitle: String = mutableTitleMapping.put(propertyName, newTitle)!!
        titledMap[newTitle] = titledMap[oldTitle]
        titledMap.remove(oldTitle)

        return true
    }


    /**
     * Internal methods
     */

    private fun updatePropertyValue(propertyName: String, value: Any?) {
        require(map.contains(propertyName)) {
            "There's no such property under this stats object! trying to set: $propertyName"
        }

        val title: String = checkNotNull(mutableTitleMapping[propertyName]) {
            "There's no title mapping for the property: $propertyName"
        }

        map[propertyName] = value
        titledMap[title] = value
    }
}

@Deprecated
(
        message = "Consider using applyThroughMainUI instead",
        replaceWith = ReplaceWith("applyWithMainUI(block)", "worder.core.model.applyWithMainUI"),
        level = DeprecationLevel.WARNING
)
inline fun <T : BaseObservableStats> T.applySynchronized(block: T.() -> Unit): T {
    synchronized(this) {
        block.invoke(this)
    }

    return this
}

suspend inline fun <T : BaseObservableStats> T.applyWithMainUI(crossinline block: suspend T.() -> Unit): T {
    MainScope().launch {
        block.invoke(this@applyWithMainUI)
    }

    return this
}
