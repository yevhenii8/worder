package worder.model.insert

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyProperty
import java.io.File

interface InsertModel {
    val statusProperty: ReadOnlyProperty<InsertModelStatus>
    val status: InsertModelStatus

    val stats: InsertModelStats

    val uncommittedUnitsProperty: ReadOnlyListProperty<InsertUnit>
    val uncommittedUnits: List<InsertUnit>


    fun generateUnits(files: List<File>): List<InsertUnit>

    suspend fun commitAllUnits()


    enum class InsertModelStatus(val description: String) {
        CREATED("InsertModel has just been created"),
        READY_TO_COMMIT("There're units that can be committed == have READY_TO_COMMIT status"),
        COMMITTED("All of the produced units are committed == have COMMITTED status"),
        PARTIALLY_COMMITTED("There're no units to commit now, but there're already committed unit(s)")
    }
}
