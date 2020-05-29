package worder.model

import kotlin.reflect.KProperty

interface Stats {
    val asMap: Map<String, Any?>
    val origin: String

    // Well, Okay. As for now only one subscriber-slot is available for property subscribing
    fun subscribe(property: String, listener: (newValue: Any?) -> Unit)

    // And unlimited slots are available for classic subscribing
    fun <T : Stats> subscribe(listener: (updatedStats: T) -> Unit)
}

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
    operator fun <T : Any?> getValue(thisRef: Any?, property: KProperty<*>): T =
            if (properties.containsKey(property.name)) properties[property.name] as T
            else throw IllegalStateException("There's no such property under stats object!")

    operator fun <T : Any?> setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (properties.containsKey(property.name)) {
            properties[property.name] = value
            listeners.forEach { it.invoke(this) }
            propertyListeners[property.name]?.invoke(value.toString())
        }
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
            this.stats = stats
            return this
        }
    }
}
