package worder.model

data class Word constructor(val name: String) {
    var transcription = ""
    var rate = 0

    val translations = mutableSetOf<String>()
    val definitions = mutableSetOf<String>()
    val examples = mutableSetOf<String>()
}