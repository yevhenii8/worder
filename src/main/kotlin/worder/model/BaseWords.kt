package worder


data class BaseWord(override val name: String) : Word {
    override var transcription: String? = null

    constructor(name: String, transcription: String?) : this(name) {
        this.transcription = transcription
    }
}


class BaseDatabaseWord(
    override val name: String,
    override val transcription: String?,

    override val rate: Int,

    override val register: Long,
    override val lastModification: Long,
    override val lastRateModification: Long,
    override val lastTraining: Int,

    override val translations: Set<String>,
    override val examples: Set<String>
) : DatabaseWord

class BaseUpdatedWord(
    override val name: String,
    override val transcription: String?,

    override val primaryDefinition: String,
    override val secondaryDefinition: String?,
    override val examples: Set<String>
) : UpdatedWord
