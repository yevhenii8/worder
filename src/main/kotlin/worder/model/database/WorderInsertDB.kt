package worder.model.database

interface WorderInsertDB {
    val inserterSessionStats: InserterSessionStats


    suspend fun containsWord(name: String): Boolean

    suspend fun insertWord(name: String): Boolean

    suspend fun resetWord(name: String): Boolean

    suspend fun resolveWord(name: String): ResolveRes {
        return if (containsWord(name)) {
            resetWord(name)
            ResolveRes.RESET
        } else {
            insertWord(name)
            ResolveRes.INSERTED
        }
    }


    enum class ResolveRes {
        INSERTED, RESET
    }
}
