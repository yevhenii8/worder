package worder.database

import worder.Word


interface LocalWordsExtractDb : LocalWordsDb {
    override val sessionStat: InserterSessionStat


    fun containsWord(name: String): Boolean
    fun containsWord(word: Word): Boolean = containsWord(word.name)


    fun addWord(name: String): Boolean
    fun addWord(word: Word): Boolean = addWord(word.name)


    fun resetWord(name: String): Boolean
    fun resetWord(word: Word): Boolean = resetWord(word.name)


    fun resolveWord(name: String): ResolveRes {
        return if (containsWord(name)) {
            resetWord(name)
            ResolveRes.RESET
        } else {
            addWord(name)
            ResolveRes.INSERTED
        }
    }
    fun resolveWord(word: Word) = resolveWord(word.name)


    enum class ResolveRes {
        INSERTED, RESET
    }
}
