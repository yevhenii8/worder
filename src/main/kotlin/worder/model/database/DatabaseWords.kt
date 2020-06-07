package worder.model.database

import worder.model.Word

class DatabaseWord(
        name: String,
        transcription: String?,

        val rate: Int,
        val register: Long,
        val lastModification: Long,
        val lastRateModification: Long,
        val lastTraining: Int,

        val translations: Set<String>,
        val examples: Set<String>
) : Word(name, transcription)

class UpdatedWord(
        name: String,
        transcription: String?,

        val primaryDefinition: String,
        val secondaryDefinition: String?,
        val examples: Set<String>
) : Word(name, transcription)
