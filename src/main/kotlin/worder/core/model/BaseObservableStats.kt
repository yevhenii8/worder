package worder.core.model

import javafx.beans.property.IntegerProperty
import javafx.beans.property.MapProperty
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyMapProperty
import javafx.beans.property.SimpleMapProperty
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tornadofx.getValue
import tornadofx.onChange
import worder.tornadofx.observableMapOf
import kotlin.reflect.KProperty

open class BaseObservableStats(override val origin: String) : ObservableStats {
    private val mutableTitleMapping: MutableMap<String, String> = mutableMapOf()
    private val mapProperty: MapProperty<String, Any?> = SimpleMapProperty(observableMapOf())
    private val map: MutableMap<String, Any?> by mapProperty
    private val titledMapProperty: MapProperty<String, Any?> = SimpleMapProperty(observableMapOf())
    private val titledMap: MutableMap<String, Any?> by titledMapProperty

    override val asMapProperty: ReadOnlyMapProperty<String, Any?> = mapProperty
    override val asMap: Map<String, Any?> = map
    override val asTitledMapProperty: ReadOnlyMapProperty<String, Any?> = titledMapProperty
    override val asTitledMap: Map<String, Any?> = titledMap


    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any?> getValue(thisRef: Any?, property: KProperty<*>): T = map[property.name] as T

    operator fun <T : Any?> setValue(thisRef: Any?, property: KProperty<*>, value: T) = updatePropertyValue(property.name, value)


    fun bindToStats(
            initValue: Any?,
            defaultTitle: String? = null
    ): BaseObservableStatsDelegate = BaseObservableStatsDelegate(initValue, defaultTitle)

    fun <T> bindToPropertyAndStats(
            source: Property<T>,
            defaultTitle: String? = null,
            usePropertyNameAsTitle: Boolean = false
    ): BasePropertyDelegate<T> = BasePropertyDelegate(source, defaultTitle, usePropertyNameAsTitle)

    fun bindToPropertyAndStats(
            source: IntegerProperty,
            defaultTitle: String? = null,
            usePropertyNameAsTitle: Boolean = false
    ): IntegerPropertyDelegate = IntegerPropertyDelegate(source, defaultTitle, usePropertyNameAsTitle)


    private fun updatePropertyValue(propertyName: String, value: Any?) {
        val title: String = mutableTitleMapping[propertyName]!!

        map[propertyName] = value
        titledMap[title] = value
    }

    private fun initPropertyTitle(propertyName: String, defaultTitle: String?): String {
        val title: String = defaultTitle ?: mutableTitleMapping[propertyName] ?: propertyName
        val titleMapping = mutableTitleMapping.entries.find { it.value == title }?.key

        // handling properties overriding and ensures that title is unique
        require(titleMapping == null || propertyName == titleMapping) {
            "Can't bind new property! Title [$title] is already presented! " +
                    "Current bound: [$title -> $titleMapping] " +
                    "Proposed bound: [$title -> $propertyName]"
        }

        mutableTitleMapping[propertyName] = title
        return title
    }


    inner class BaseObservableStatsDelegate(
            private val initValue: Any?,
            private val defaultTitle: String?
    ) {
        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): BaseObservableStats {
            initPropertyTitle(property.name, defaultTitle)
            updatePropertyValue(property.name, initValue)
            return this@BaseObservableStats
        }
    }

    inner class BasePropertyDelegate<T>(
            private val source: Property<T>,
            private val defaultTitle: String?,
            private val usePropertyNameAsTitle: Boolean
    ) {
        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): Property<T> {
            initPropertyTitle(
                    property.name,
                    if (usePropertyNameAsTitle && source.name.isNotBlank()) source.name else defaultTitle
            )

            updatePropertyValue(property.name, source.value)

            source.onChange {
                updatePropertyValue(property.name, it)
            }

            return source
        }
    }

    inner class IntegerPropertyDelegate(
            private val source: IntegerProperty,
            private val defaultTitle: String?,
            private val usePropertyNameAsTitle: Boolean
    ) {
        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): IntegerProperty {
            initPropertyTitle(
                    property.name,
                    if (usePropertyNameAsTitle && source.name.isNotBlank()) source.name else defaultTitle
            )

            updatePropertyValue(property.name, source.value)

            source.onChange {
                updatePropertyValue(property.name, it)
            }

            return source
        }
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
