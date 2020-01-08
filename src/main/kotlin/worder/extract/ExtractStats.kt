package worder.extract

import worder.AbstractStat


sealed class FileExtractStat(fileName: String, totalWords: Int, newWords: Int) : AbstractStat() {
    override val map: Map<String, String> = mapOf(
        "fileName" to fileName,
        "totalWords" to totalWords.toString(),
        "newWords" to newWords.toString()
    )
}


class AddingFileStat(
    override val origin: String,
    fileName: String,
    totalWords: Int,
    alreadyPresent: Int,
    newWords: Int
) : FileExtractStat(fileName, totalWords, newWords)
{
    override val map: Map<String, String> = super.map + ("alreadyPresent" to alreadyPresent.toString())
}

class ResolvingFileStat(
    override val origin: String,
    fileName: String,
    totalWords: Int,
    newWords: Int,
    resetWords: Int
) : FileExtractStat(fileName, totalWords, newWords)
{
    override val map: Map<String, String> = super.map + ("resetWords" to resetWords.toString())
}
