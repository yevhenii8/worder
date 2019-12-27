package worder.database

import worder.model.BaseWord

interface WordsInsertDB : WordsDB {
    override val sessionStat: InserterSessionStat

    fun resolveWord(word: BaseWord)
}
