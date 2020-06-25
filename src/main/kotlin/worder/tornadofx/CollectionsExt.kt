package worder.tornadofx

import javafx.collections.FXCollections
import javafx.collections.ObservableMap
import java.util.*
import kotlin.collections.HashMap

/**
 * Returns a new empty [ObservableMap] that is backed by a LinkedHashMap
 */
fun <K, V> observableMapOf(): ObservableMap<K, V> = FXCollections.observableMap(LinkedHashMap())

/**
 * Returns an empty new [ObservableMap] that is backed by a HashMap
 */
fun <K, V> observableHashMapOf(): ObservableMap<K, V> = FXCollections.observableMap(HashMap())
