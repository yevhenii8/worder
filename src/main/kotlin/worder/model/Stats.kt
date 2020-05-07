package worder.model

interface Stats {
    val asMap: Map<String, Any>
    val origin: String

    fun subscribe(property: String, listener: (newValue: String) -> Unit)
}

abstract class AbstractStats : Stats {
    private val properties = LinkedHashMap<String, Any>()
    private val listeners = LinkedHashMap<String, (newValue: String) -> Unit>()

    protected val map: MutableMap<String, Any> = object : MutableMap<String, Any> by properties {
        override fun put(key: String, value: Any): Any? {
            val oldValue = properties[key]
            properties[key] = value
            listeners[key]?.invoke(value.toString())
            return oldValue
        }
    }

    override val asMap: Map<String, Any> = map

    override fun subscribe(property: String, listener: (newValue: String) -> Unit) {
        listeners[property] = listener
    }
}
