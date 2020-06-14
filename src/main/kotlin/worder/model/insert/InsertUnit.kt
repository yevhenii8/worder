package worder.model.insert

import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.ReadOnlyStringProperty
import worder.model.BareWord

interface InsertUnit {
    val idProperty: ReadOnlyStringProperty
    val id: String

    val unitStatusProperty: ReadOnlyProperty<InsertUnitStatus>
    val unitStatus: InsertUnitStatus

    val sourceProperty: ReadOnlyStringProperty
    val source: String

    val validWordsProperty: ReadOnlyListProperty<BareWord>
    val validWords: List<BareWord>

    val invalidWordsProperty: ReadOnlyListProperty<InvalidWord>
    val invalidWords: List<InvalidWord>


    suspend fun commit()

    fun excludeFromCommit()

    fun includeInCommit()


    interface InvalidWord {
        val value: String

        fun reject()
        fun substitute(substitution: String): Boolean
    }


    enum class InsertUnitStatus(val description: String) {
        READY_TO_COMMIT("Unit can be committed either by model or by itself"),
        ACTION_NEEDED("Invalid words should be rejected or substituted"),
        EXCLUDED_FROM_COMMIT("Unit can not be committed"),
        COMMITTING("Unit's committing is in progress"),
        COMMITTED("Unit has been committed")
    }
}
