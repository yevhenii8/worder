/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <BatchUnit.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <7>
 */

package worder.gui.insert.model

import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.ReadOnlySetProperty
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import worder.gui.core.model.BareWord
import worder.gui.core.model.WorderStatus

interface BatchUnit {
    val id: String
    val source: String

    val statusProperty: ReadOnlyProperty<BatchUnitStatus>
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


    enum class BatchUnitStatus(override val description: String, override val color: Paint, val availableActions: Array<BatchUnitAction>) : WorderStatus {
        READY_TO_COMMIT("Unit can be committed either by model or by itself", Color.GREEN, arrayOf(BatchUnitAction.COMMIT, BatchUnitAction.EXCLUDE)),
        ACTION_NEEDED("Invalid words should be rejected or substituted", Color.RED, arrayOf(BatchUnitAction.EXCLUDE)),
        EXCLUDED_FROM_COMMIT("Unit can not be committed", Color.DIMGRAY, arrayOf(BatchUnitAction.INCLUDE)),
        COMMITTING("Unit's committing is in progress", Color.DARKORANGE, arrayOf()),
        COMMITTED("Unit has been committed", Color.DIMGRAY, arrayOf());
    }

    enum class BatchUnitAction {
        COMMIT, EXCLUDE, INCLUDE
    }
}
