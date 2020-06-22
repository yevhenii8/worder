package worder.model

import java.util.*

open class BareWord(val name: String) {
    companion object {
        private val wordPattern = "([a-z\\-']+)|(([a-z\\-']+ [a-z\\-']+)+( [a-z\\-']+)?)".toRegex()
        val wordValidator: (word: String) -> Boolean = { word -> word.matches(wordPattern) }
    }


    init {
        require(wordValidator.invoke(name)) {
            "Invalid word's name: $name"
        }
    }


    override fun toString(): String = "BareWord(name=$name)"
    override fun hashCode(): Int = name.hashCode()
    override fun equals(other: Any?): Boolean =
            if (other is BareWord) other.name == name else false
}

open class Word(name: String, val transcription: String?) : BareWord(name) {
    override fun toString(): String = "Word(name=$name, transcription=$transcription)"
    override fun hashCode(): Int = Objects.hash(name, transcription)
    override fun equals(other: Any?): Boolean =
            if (other is Word) other.name == name && other.transcription == transcription else false
}
