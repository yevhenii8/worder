package worder.insert.model

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.ReadOnlySetProperty
import javafx.scene.paint.Color
import worder.core.model.Status

interface InsertModel {
    val modelStatusProperty: ReadOnlyProperty<InsertModelStatus>
    val modelStatus: InsertModelStatus

    val observableStats: ObservableInsertModelStats

    val uncommittedUnitsProperty: ReadOnlySetProperty<InsertUnit>
    val uncommittedUnits: Set<InsertUnit>

    val committedUnitsProperty: ReadOnlySetProperty<InsertUnit>
    val committedUnits: Set<InsertUnit>


    suspend fun commitAllUnits()


    enum class InsertModelStatus(override val description: String, override val color: Color) : Status {
        CREATED("InsertModel has just been created", Color.DIMGRAY),
        READY_TO_COMMIT("There're units that can be committed == have READY_TO_COMMIT status", Color.GREEN),
        COMMITTED("All of the produced units are committed == have COMMITTED status", Color.DIMGRAY),
        PARTIALLY_COMMITTED("There're no units to commit now, but there're already committed unit(s)", Color.DARKORANGE);
    }
}
