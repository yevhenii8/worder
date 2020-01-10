package worder.request

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import worder.BaseWord
import worder.request.implementations.Cambridge
import worder.request.implementations.Lingvo
import worder.request.implementations.Macmillan
import kotlin.system.measureTimeMillis

class RequesterTest {
    @Test
    fun allRequestersTest() {
        val word1 = BaseWord("diverse")
        val word2 = BaseWord("escort")
        val word3 = BaseWord("bias")

        val cambridge1 = Cambridge.instance
        val cambridge2 = Cambridge.instance
        val cambridge3 = Cambridge.instance

        println(measureTimeMillis {
            runBlocking {
                launch { cambridge1.requestWord(word3) }
                launch { cambridge2.requestWord(word2) }
                launch { cambridge3.requestWord(word1) }
            }
        })

//        requesters.forEach {
//            it.acceptWord(word1)
//
//            if (it is DefinitionRequester) {
//                val definitions = it.getDefinitions()
//                println("$it definitions: $definitions => ${definitions.size}")
//            }
//            if (it is TranslationRequester) {
//                val translations = it.getTranslations()
//                println("$it translations: $translations => ${translations.size}")
//            }
//            if (it is ExampleRequester) {
//                val examples = it.getExamples()
//                println("$it examples: $examples => ${examples.size}")
//            }
//
//            println(it.sessionStat)
//
//            println()
//            println()
//        }
//
//        requesters.forEach {
//            it.acceptWord(word2)
//
//            if (it is DefinitionRequester) {
//                val definitions = it.getDefinitions()
//                println("$it definitions: $definitions => ${definitions.size}")
//            }
//            if (it is TranslationRequester) {
//                val translations = it.getTranslations()
//                println("$it translations: $translations => ${translations.size}")
//            }
//            if (it is ExampleRequester) {
//                val examples = it.getExamples()
//                println("$it examples: $examples => ${examples.size}")
//            }
//
//            println(it.sessionStat)
//
//            println()
//            println()
//        }
//
//        requesters.forEach {
//            it.acceptWord(word3)
//
//            if (it is DefinitionRequester) {
//                val definitions = it.getDefinitions()
//                println("$it definitions: $definitions => ${definitions.size}")
//            }
//            if (it is TranslationRequester) {
//                val translations = it.getTranslations()
//                println("$it translations: $translations => ${translations.size}")
//            }
//            if (it is ExampleRequester) {
//                val examples = it.getExamples()
//                println("$it examples: $examples => ${examples.size}")
//            }
//
//            println(it.sessionStat)
//
//            println()
//            println()
//        }
    }
}
