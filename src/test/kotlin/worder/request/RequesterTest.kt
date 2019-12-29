package worder.request

import org.junit.Test
import worder.model.Word

class RequesterTest {
    @Test
    fun allRequestersTest() {
        val requesters = Requester.getAllKnownImplementation()
//        val word = Word("excel")
//        val word = Word("grill")
        val word1 = Word("diverse")
        val word2 = Word("escort")
        val word3 = Word("bias")

        requesters.forEach {
            it.acceptWord(word1)

            if (it is DefinitionRequester) {
                val definitions = it.getDefinitions()
                println("$it definitions: $definitions => ${definitions.size}")
            }
            if (it is TranslationRequester) {
                val translations = it.getTranslations()
                println("$it translations: $translations => ${translations.size}")
            }
            if (it is ExampleRequester) {
                val examples = it.getExamples()
                println("$it examples: $examples => ${examples.size}")
            }

            println(it.sessionStat)

            println()
            println()
        }

        requesters.forEach {
            it.acceptWord(word2)

            if (it is DefinitionRequester) {
                val definitions = it.getDefinitions()
                println("$it definitions: $definitions => ${definitions.size}")
            }
            if (it is TranslationRequester) {
                val translations = it.getTranslations()
                println("$it translations: $translations => ${translations.size}")
            }
            if (it is ExampleRequester) {
                val examples = it.getExamples()
                println("$it examples: $examples => ${examples.size}")
            }

            println(it.sessionStat)

            println()
            println()
        }

        requesters.forEach {
            it.acceptWord(word3)

            if (it is DefinitionRequester) {
                val definitions = it.getDefinitions()
                println("$it definitions: $definitions => ${definitions.size}")
            }
            if (it is TranslationRequester) {
                val translations = it.getTranslations()
                println("$it translations: $translations => ${translations.size}")
            }
            if (it is ExampleRequester) {
                val examples = it.getExamples()
                println("$it examples: $examples => ${examples.size}")
            }

            println(it.sessionStat)

            println()
            println()
        }
    }
}
