package worder.model


interface Word {
    val name: String
    val transcription: String?
}


interface DatabaseWord : Word {
    val rate: Int

    val register: Long
    val lastModification: Long
    val lastRateModification: Long
    val lastTraining: Int

    val translations: Set<String>
    val examples: Set<String>
}

interface UpdatedWord : Word {
    val primaryDefinition: String
    val secondaryDefinition: String?
    val examples: Set<String>
}
