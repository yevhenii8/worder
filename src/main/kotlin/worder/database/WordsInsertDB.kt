package worder.database

import worder.model.Word


interface WordsInsertDB : WordsDB {
    override val sessionStat: InserterSessionStat


    fun containsWord(word: Word) : Boolean

    fun insertWord(word: Word)

    fun resetWord(word: Word)

    fun resolveWord(word: Word) = if (containsWord(word)) resetWord(word) else insertWord(word)
}
