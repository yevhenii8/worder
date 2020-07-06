/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <Words.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <06/07/2020, 07:25:08 PM>
 * Version: <3>
 */

package worder.core.model

import java.util.*

open class BareWord(val name: String) {
    companion object {
        private val wordPattern = "([a-zA-Z\\-']+)|(([a-zA-Z\\-']+ [a-zA-Z\\-']+)+( [a-zA-Z\\-']+)?)".toRegex()
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
