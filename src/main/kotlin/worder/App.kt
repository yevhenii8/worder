/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package worder

import worder.model.Word
import worder.request.DefinitionRequester
import worder.request.ExampleRequester
import worder.request.Requester
import worder.request.TranslationRequester
import worder.request.sites.Lingvo
import worder.request.sites.Macmillan
import worder.request.sites.WooordHunt

class App {
    val greeting: String
        get() {
            return "Hello world."
        }
}

fun main(args: Array<String>) {
    val requesters = Requester.getAllKnownImplementation()
    val word = Word("slab")

    requesters.apply {
        forEach { it.acceptWord(word) }

        forEach {
               if (it is DefinitionRequester)
                   println("$it: ${it.getDefinitions()}")
               if (it is TranslationRequester)
                   println("$it: ${it.getTranslations()}")
               if (it is ExampleRequester)
                   println("$it: ${it.getExamples()}")
           }

        println()
        println()

        forEach { println("$it: ${it.sessionStat}") }
    }
}
