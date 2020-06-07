package worder.model

import java.util.*

open class BareWord(val name: String) {
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
