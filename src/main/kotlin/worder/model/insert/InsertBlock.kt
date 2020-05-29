package worder.model.insert

import java.io.File

interface InsertBlock {
    val file: File
    val isCommitted: Boolean

    val wordsToReset: Set<String>
    val wordsToInsert: Set<String>

    fun commit()
}
