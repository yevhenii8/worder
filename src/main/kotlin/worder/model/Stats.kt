package worder.model

interface Stats<T> {
    val origin: String

    fun subscribe(tracer: T.() -> Unit)
    fun unsubscribe(tracer: T.() -> Unit)
}
