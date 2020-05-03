package worder.model

interface Stats<T> {
    val subscribers: MutableList<T.() -> Unit>
    val origin: String

    fun subscribe(tracer: T.() -> Unit): Boolean
    fun unsubscribe(tracer: T.() -> Unit): Boolean
}

abstract class AbstractStats<T> : Stats<T> {
    override val subscribers: MutableList<T.() -> Unit> = ArrayList()

    override fun subscribe(tracer: T.() -> Unit) = subscribers.add(tracer)
    override fun unsubscribe(tracer: T.() -> Unit) = subscribers.remove(tracer)
}
