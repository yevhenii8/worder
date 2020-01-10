package worder.update

import worder.DatabaseWord

interface WordBlock {
    val dbWord: DatabaseWord

    val definitions: Set<String>
    val examples: Set<String>
    val translations: Set<String>
    val transcriptions: Set<String>

    var status: BlockStatus
    val serialNumber: Int
    val resolution: String?


    // returns false if it would be called when status == COMMITTED

    fun skip() : Boolean

    fun remove() : Boolean

    fun learn() : Boolean

    fun update(
        primaryDefinition: String,
        secondaryDefinition: String?,
        examples: Set<String>,
        transcription: String?
    ) : Boolean


    enum class BlockStatus {
        COMMITTED, READY_TO_COMMIT, WAITING_FOR_RESOLUTION, READY_FOR_RESOLUTION
    }
}
