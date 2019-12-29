package worder.model

open class Word(val name: String, val transcription: String? = null) {
    override fun hashCode() = name.hashCode()
    override fun toString() = "BaseWord(name: $name, transcription: $transcription)"
    override fun equals(other: Any?) = if (other is Word) name == other.name else false
}

class UpdatedWord(
    name: String,
    transcription: String?,
    val primaryDefinition: String,
    val secondaryDefinition: String?,
    val examples: MutableSet<String> = mutableSetOf()
) : Word(name, transcription)

class DatabaseWord(
    name: String,
    transcription: String?,
    val rate: Int,
    val register: Long,
    val lastModification: Long,
    val lastRateModification: Long,
    val lastTraining: Int,
    val translations: MutableSet<String> = mutableSetOf(),
    val examples: MutableSet<String> = mutableSetOf()
) : Word(name, transcription)
