package worder.model.insert

interface InsertUnit {
    val insertUnitStats: InsertUnitStats
    val invalidWords: Set<String>
    val validWords: Set<String>

    fun excludeFromBatch()
    fun includeInBatch()

    suspend fun process()
}
