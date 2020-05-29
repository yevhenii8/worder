package worder.model.insert

import worder.model.database.WorderInsertDB
import worder.model.insert.InsertModel.InsertionMode.WORD_AS_LINE
import worder.model.insert.InsertModel.InsertionMode.WORD_AS_WORD
import java.io.File
import java.lang.IllegalStateException


class InsertModel(val database: WorderInsertDB) {
    fun processFiles(files: List<File>, mode: InsertionMode): List<InsertBlock> {
        files.forEach {
            if (!(it.isFile && it.canRead()))
                throw IllegalArgumentException("Please provide correct readable file!")
        }

        val wordsToInsert = mutableSetOf<String>()
        val wordsToReset = mutableSetOf<String>()

        return when (mode) {
            WORD_AS_LINE -> {
                files.map { file ->
                    file.readLines()
                            .filterNot { it.isBlank() }
                            .forEach { word ->
                                if (database.containsWord(word))
                                    wordsToReset.add(word)
                                else
                                    wordsToInsert.add(word)
                            }

                    BaseInsertBlock(file, wordsToReset, wordsToInsert)
                }
            }

            WORD_AS_WORD -> {
                files.map { file ->
                    file.readText()
                            .replace("[^a-zA-Z\\-']".toRegex(), " ")
                            .split(" ")
                            .filterNot { it.isBlank() }
                            .map { it.toLowerCase() }
                            .map { word ->
                                if (database.containsWord(word))
                                    wordsToReset.add(word)
                                else
                                    wordsToInsert.add(word)
                            }

                    BaseInsertBlock(file, wordsToReset, wordsToInsert)
                }
            }
        }
    }

    private inner class BaseInsertBlock(
            override val file: File,
            override val wordsToReset: Set<String>,
            override val wordsToInsert: Set<String>
    ) : InsertBlock {
        override var isCommitted: Boolean = false
            private set

        override fun commit() {
            if (isCommitted)
                throw IllegalStateException("InsertBlock (${file.name}): block has already been committed!")

            wordsToInsert.forEach { database.insertWord(it) }
            wordsToReset.forEach { database.resetWord(it) }

            isCommitted = true
        }
    }

    enum class InsertionMode {
        WORD_AS_LINE, WORD_AS_WORD
    }
}
