package worder.database

import worder.model.Word

interface WordsDB {
    val sessionStat: DbSessionStat
    val worderTrack: DbWorderTrack
    val summary: DbSummary

    val allStats
        get() = listOf(sessionStat, worderTrack, summary)

    fun getNextWord(order: SelectOrder): Word

    fun updateWord(word: Word)
    fun ignoreWord(word: Word)
    fun removeWord(word: Word)

    enum class SelectOrder {
        ASC, DESC, RANDOM
    }
}