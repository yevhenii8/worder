package worder.model

import kotlin.reflect.KProperty

interface Stats {
    val asMap: Map<String, Any>
    val origin: String

    fun subscribe(property: String, listener: (newValue: Any) -> Unit)
}

class SharedStats(override val origin: String) : Stats {
    private val properties = LinkedHashMap<String, Any>()
    private val listeners = LinkedHashMap<String, (newValue: Any) -> Unit>()


    override val asMap: Map<String, Any> = properties
    override fun subscribe(property: String, listener: (newValue: Any) -> Unit) {
        if (properties.containsKey(property))
            listeners[property] = listener
    }


    @Suppress("UNCHECKED_CAST")
    operator fun <T> getValue(thisRef: Any?, property: KProperty<*>): T = properties[property.name] as T
            ?: throw IllegalStateException("Property ${property.name} hasn't been initialized!")

    operator fun <T: Any> setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        properties[property.name] = value
        listeners[property.name]?.invoke(value.toString())
    }
}