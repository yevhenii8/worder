package worder.model.database

interface WorderInsertDB {
    val inserterSessionStats: InserterSessionStats


    fun containsWord(name: String): Boolean

    fun insertWord(name: String): Boolean

    fun resetWord(name: String): Boolean

    fun resolveWord(name: String): ResolveRes {
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
