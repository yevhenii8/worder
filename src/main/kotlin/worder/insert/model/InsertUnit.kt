package worder.insert.model

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.ReadOnlySetProperty
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import worder.core.model.BareWord
import worder.core.model.Status

interface InsertUnit {
    val id: String
    val source: String

    val statusProperty: ReadOnlyProperty<InsertUnitStatus>
    val validWordsProperty: ReadOnlySetProperty<BareWord>
    val invalidWordsProperty: ReadOnlySetProperty<InvalidWord>


    suspend fun commit()
    fun exclude()
    fun include()


    interface InvalidWord {
        val value: String

        fun reject()
        fun substitute(substitution: String): Boolean
    }


    enum class InsertUnitStatus(override val description: String, override val color: Paint, val availableActions: Array<InsertUnitAction>) : Status {
        READY_TO_COMMIT("Unit can be committed either by model or by itself", Color.GREEN, arrayOf(InsertUnitAction.COMMIT, InsertUnitAction.EXCLUDE)),
        ACTION_NEEDED("Invalid words should be rejected or substituted", Color.RED, arrayOf(InsertUnitAction.EXCLUDE)),
        EXCLUDED_FROM_COMMIT("Unit can not be committed", Color.DIMGRAY, arrayOf(InsertUnitAction.INCLUDE)),
        COMMITTING("Unit's committing is in progress", Color.DARKORANGE, arrayOf()),
        COMMITTED("Unit has been committed", Color.DIMGRAY, arrayOf());
    }

    enum class InsertUnitAction {
        COMMIT, EXCLUDE, INCLUDE
    }
}
