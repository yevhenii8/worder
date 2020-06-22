package worder.core.model

import kotlin.reflect.KProperty

@Deprecated(message = "Use BaseObservableStats instead", level = DeprecationLevel.HIDDEN)
@Suppress("DEPRECATION_ERROR")
open class SharedStats(override val origin: String) : Stats {
    private val properties = LinkedHashMap<String, Any?>()
    private val propertyListeners = LinkedHashMap<String, (newValue: Any?) -> Unit>()
    private val listeners = mutableListOf<(updatedStats: Stats) -> Unit>()


    override val asMap: Map<String, Any?> = properties

    override fun subscribe(property: String, listener: (newValue: Any?) -> Unit) {
        if (properties.containsKey(property))
            propertyListeners[property] = listener
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Stats> subscribe(listener: (updatedStats: T) -> Unit) {
        listeners.add(listener as (Stats) -> Unit)
    }


    @Suppress("UNCHECKED_CAST")
    operator fun <T : Any?> getValue(thisRef: Any?, property: KProperty<*>): T {
        require(properties.containsKey(property.name)) {
            "There's no such property under the stats object! trying to get: ${property.name}"
        }

        return properties[property.name] as T
    }

    operator fun <T : Any?> setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        requireNotNull(properties.containsKey(property.name)) {
            "There's no such property under the stats object! requested property: ${property.name}"
        }

        properties[property.name] = value
        listeners.forEach { it.invoke(this) }
        propertyListeners[property.name]?.invoke(value.toString())
    }


    object SharedStatsBinder {
        private var initVal: Any? = null
        private var stats: SharedStats? = null

        operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): SharedStats {
            stats?.properties?.set(property.name, initVal)
            return stats!!
        }

        fun bind(stats: SharedStats, initValue: Any?): SharedStatsBinder {
            initVal = initValue
            SharedStatsBinder.stats = stats
            return this
        }
    }
}
