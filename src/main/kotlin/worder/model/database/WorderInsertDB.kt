package worder.model.database

interface WorderInsertDB {
    val inserterSessionStat: InserterSessionStat


    fun containsWord(name: String): Boolean

    fun addWord(name: String): Boolean

    fun resetWord(name: String): Boolean

    fun resolveWord(name: String): ResolveRes {
        return if (containsWord(name)) {
            resetWord(name)
            ResolveRes.RESET
        } else {
            addWord(name)
            ResolveRes.INSERTED
        }
    }


    enum class ResolveRes {
        INSERTED, RESET
    }
}
