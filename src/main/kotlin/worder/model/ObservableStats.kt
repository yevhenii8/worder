package worder.model

import javafx.beans.property.ReadOnlyMapProperty

interface ObservableStats {
    val origin: String

    val asMapProperty: ReadOnlyMapProperty<String, Any?>
    val asMap: Map<String, Any?>

    val asTitledMapProperty: ReadOnlyMapProperty<String, Any?>
    val asTitledMap: Map<String, Any?>

    val titleMapping: Map<String, String>


    fun updateTitle(propertyName: String, newTitle: String): Boolean
}
