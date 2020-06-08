package worder.model.insert

import worder.model.BareWord

interface InsertUnit {
    val id: String
    val status: InsertUnitStatus
    val stats: InsertUnitStats

    val invalidWords: Set<InvalidWord>
    val validWords: Set<BareWord>


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
