package worder.update

import worder.DatabaseWord

interface WordBlock {
    val definitions: Set<String>
    val examples: Set<String>
    val translations: Set<String>
    val transcriptions: Set<String>

    val dbWord: DatabaseWord
    val serialNumber: Int
    val isCommitted: Boolean
    val resolution: String


    // returns false if it would be called when isCommitted() == TRUE

    fun skip() : Boolean
    fun remove() : Boolean
    fun learned() : Boolean

    fun update(
        primaryDefinition: String,
        secondaryDefinition: String?,
        examples: Set<String>,
        transcription: String?
    ) : Boolean
}
