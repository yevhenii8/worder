package worder.model

open class BaseWord(val name: String, val transcription: String?) {
    override fun hashCode() = name.hashCode()
    override fun toString() = "BaseWord(name: $name, transcription: $transcription)"
    override fun equals(other: Any?) = if (other is BaseWord) name == other.name else false
}

class UpdatedWord(
    name: String,
    transcription: String?,
    val primaryDefinition: String,
    val secondaryDefinition: String?,
    val examples: MutableSet<String> = mutableSetOf()
) : BaseWord(name, transcription)

class DatabaseWord(
    name: String,
    transcription: String?,
    val translations: MutableSet<String> = mutableSetOf(),
    val examples: MutableSet<String> = mutableSetOf(),
    val rate: Int,
    val register: Long,
    val lastModification: Long,
    val lastRateModification: Long,
    val lastTraining: Int
) : BaseWord(name, transcription)
