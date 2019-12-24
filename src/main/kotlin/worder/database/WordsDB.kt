package worder.database

import worder.model.Word

interface WordsDB {
    val updatedTag: String
        get() = "(W) Updated"
    val insertedTag: String
        get() = "(W) Inserted"
    val resetTag: String
        get() = "(W) Reset"

    val usingStat: DbUsingStat
    val summary: DbSummary

    fun getNextWords(count: Int, order: SelectOrder): List<Word>
    fun getNextWord(order: SelectOrder): Word {
        return getNextWords(1, order).first()
    }

    fun updateWords(words: Collection<Word>)
    fun updateWord(word: Word) {
        updateWords(listOf(word))
    }

    fun ignoreWords(words: Collection<Word>)
    fun ignoreWord(word: Word) {
        ignoreWords(listOf(word))
    }

    fun removeWords(words: Collection<Word>)
    fun removeWord(word: Word) {
        removeWords(listOf(word))
    }

    enum class SelectOrder {
        ASC, DESC, RANDOM
    }
}