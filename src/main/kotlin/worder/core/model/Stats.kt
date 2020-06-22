package worder.core.model

@Deprecated(message = "Use ObservableStats instead", level = DeprecationLevel.HIDDEN)
@Suppress("DEPRECATION_ERROR")
interface Stats {
    val asMap: Map<String, Any?>
    val origin: String

    // Well, Okay. As for now only one subscriber-slot is available for property subscribing
    fun subscribe(property: String, listener: (newValue: Any?) -> Unit)

    // And unlimited amount of slots are available for classic subscribing
    fun <T : Stats> subscribe(listener: (updatedStats: T) -> Unit)
}
