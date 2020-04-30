package worder.model.extract

import worder.model.database.LocalWordsExtractDb
import java.io.File


class ExtractModel(val database: LocalWordsExtractDb) {
    fun addNewWords(file: File): AddingFileStat {
        checkFile(file)

        val allWords = file.readText()
            .split(" ")
            .map { it.replace("[.,]".toRegex(), "").toLowerCase() }
            .toSet()

        val totalWords = allWords.count()
        val newWords = allWords.filter { database.addWord(it) }.count()

        return AddingFileStat(
            origin = this.javaClass.simpleName,
            fileName = file.name,
            totalWords = allWords.count(),
            alreadyPresent = totalWords - newWords,
            newWords = newWords
        )
    }

    fun resolveAllWords(file: File): ResolvingFileStat {
        checkFile(file)

        val allWords = file.readText()
            .split(" ")
            .map { it.replace("[.,]".toRegex(), "").toLowerCase() }
            .toSet()

        val totalWords = allWords.count()
        val newWords = allWords.filter { database.resolveWord(it) == LocalWordsExtractDb.ResolveRes.INSERTED }.count()

        return ResolvingFileStat(
            origin = this.javaClass.simpleName,
            fileName = file.name,
            totalWords = allWords.count(),
            newWords = newWords,
            resetWords = totalWords - newWords
        )
    }


    private fun checkFile(file: File) {
        if (!(file.isFile && file.canRead()))
            throw IllegalArgumentException("Please provide correct readable file!")
    }
}
