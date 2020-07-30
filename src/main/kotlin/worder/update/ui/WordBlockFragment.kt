/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WordBlockFragment.kt>
 * Created: <24/07/2020, 07:45:55 PM>
 * Modified: <31/07/2020, 12:06:33 AM>
 * Version: <331>
 */

package worder.update.ui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ComboBox
import javafx.scene.input.KeyCode
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import tornadofx.Fragment
import tornadofx.View
import tornadofx.box
import tornadofx.combobox
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.label
import tornadofx.observableListOf
import tornadofx.onChange
import tornadofx.onChangeOnce
import tornadofx.paddingVertical
import tornadofx.px
import tornadofx.separator
import tornadofx.style
import tornadofx.toObservable
import tornadofx.tooltip
import tornadofx.vbox
import tornadofx.webview
import worder.core.formatted
import worder.core.worderStatusLabel
import worder.database.model.DatabaseWord
import worder.update.model.WordBlock
import java.time.Instant

class WordBlockFragment : Fragment() {
    private val block: WordBlock by param()
    private val clFragment: CommandLineFragment by param()

    private val word: DatabaseWord = block.originalWord
    private val possibleResolutions: ObservableList<WordBlock.WordBlockResolution>
    private val resolutionUI: ComboBox<WordBlock.WordBlockResolution>
    private val clHandler: ClHandler = ClHandler()

    private val allDefinitions: ObservableList<String> = FXCollections.observableList(DistinctArrayList(block.definitions))
    private val allExamples: ObservableList<String> = FXCollections.observableList(DistinctArrayList(block.examples))

    private val chosenTranscription: String? = block.transcriptions.firstOrNull()
    private val chosenDefinitions: ObservableList<Int> = observableListOf()
    private val chosenExamples: ObservableList<Int> = observableListOf()


    init {
        with(block.statusProperty) {
            onChange { status ->
                if (status == WordBlock.WordBlockStatus.COMMITTED)
                    root.isDisable = true
            }
        }

        possibleResolutions = WordBlock.WordBlockResolution.values()
                .filter { it != WordBlock.WordBlockResolution.UPDATED }
                .toList()
                .toObservable()

        resolutionUI = combobox(values = possibleResolutions) {
            value = block.resolution

            valueProperty().onChange { chosenResolution ->
                when (chosenResolution) {
                    WordBlock.WordBlockResolution.UPDATED -> block.update(
                            primaryDefinition = allDefinitions.elementAt(chosenDefinitions.elementAt(0)),
                            secondaryDefinition = if (chosenDefinitions.size > 1) allDefinitions.elementAt(chosenDefinitions.elementAt(1)) else null,
                            transcription = chosenTranscription,
                            examples = chosenExamples.map { allExamples.elementAt(it) }
                    )
                    WordBlock.WordBlockResolution.REMOVED -> block.remove()
                    WordBlock.WordBlockResolution.LEARNED -> block.learn()
                    WordBlock.WordBlockResolution.SKIPPED -> block.skip()
                    WordBlock.WordBlockResolution.NO_RESOLUTION -> error("You can't change presented resolution with NO_RESOLUTION!")
                }
            }

            valueProperty().onChangeOnce {
                possibleResolutions.remove(WordBlock.WordBlockResolution.NO_RESOLUTION)
            }
        }

        chosenDefinitions.onChange {
            val newSize = it.list.size

            when {
                newSize > 0 && !possibleResolutions.contains(WordBlock.WordBlockResolution.UPDATED) -> {
                    possibleResolutions.add(WordBlock.WordBlockResolution.UPDATED)
                }
                newSize == 0 -> possibleResolutions.remove(WordBlock.WordBlockResolution.UPDATED)
            }
        }
    }


    override val root: Parent = vbox(spacing = 10) {
        style {
            alignment = Pos.TOP_CENTER
            padding = box(15.px)
            borderRadius += box(15.px)
            borderColor += box(Color.GRAY)
        }

        hbox(20) {
            label(block.id) {
                style {
                    fontSize = 20.px
                    alignment = Pos.TOP_CENTER
                    padding = box(20.px)
                    borderColor += box(Color.GRAY)
                }
            }

            vbox {
                paddingVertical = 10

                label("${word.name.toUpperCase()} (?)") {
                    style {
                        fontSize = 16.px
                        padding = box(0.px, 0.px, 5.px, 0.px)
                    }

                    tooltip {
                        graphic = hbox {
                            vbox(alignment = Pos.BASELINE_RIGHT) {
                                label("Rate: ")
                                label("Registered: ")
                                label("Last Modified: ")
                                label("Last Rate Modified: ")
                                label("Last Trained: ")
                            }
                            vbox(alignment = Pos.BASELINE_LEFT) {
                                label("${word.rate} %")
                                label(Instant.ofEpochMilli(word.registered).formatted())
                                label(Instant.ofEpochMilli(word.lastModified).formatted())
                                label(Instant.ofEpochMilli(word.lastRateModified).formatted())
                                label(if (word.lastTrained > 0) Instant.ofEpochMilli(word.lastTrained).formatted() else "-")
                            }
                        }
                    }
                }
                label(block.translations.joinToString(", ")).style { fontSize = 12.px }
                label(block.transcriptions.joinToString(", ") { "[$it]" }).style { fontSize = 12.px }
            }

            hbox(alignment = Pos.CENTER_RIGHT) {
                hgrow = Priority.ALWAYS
                vbox(spacing = 5, alignment = Pos.CENTER) {
                    worderStatusLabel(block.statusProperty).style { fontSize = 16.px }
                    add(resolutionUI)
                }
            }
        }

        separator()

        add(find<ChoosablesTableFragment>(
                "allValues" to allDefinitions,
                "chosenValues" to chosenDefinitions,
                "chooseLimit" to 2
        ))

        separator()

        add(find<ChoosablesTableFragment>(
                "allValues" to allExamples,
                "chosenValues" to chosenExamples,
                "chooseLimit" to Int.MAX_VALUE
        ))
    }


    private inner class ClHandler {
        private var areDefinitionsSet = false
        private var areExamplesSet = false


        init {
            clFragment.apply {
                label.text = "DEF"
                textField.setOnKeyPressed {
                    if (it.code == KeyCode.ENTER) {
                        clHandler.send(clFragment.textField.text ?: "")
                        clFragment.textField.text = null
                    }
                }
                button.setOnAction {
                    find<WordBlockHelp>().openModal()
                }
            }
        }


        fun send(input: String) {
            if (input.startsWith("--")) {
                when (input) {
                    "--remove" -> resolutionUI.value = WordBlock.WordBlockResolution.REMOVED
                    "--learn" -> resolutionUI.value = WordBlock.WordBlockResolution.LEARNED
                    "--skip" -> resolutionUI.value = WordBlock.WordBlockResolution.SKIPPED
                    "--help" -> clFragment.button.onAction.handle(null)
                }
                return
            }

            if (!areDefinitionsSet) {
                when {
                    input.isEmpty() -> {
                        areDefinitionsSet = chosenDefinitions.size > 0
                    }

                    input.first().isDigit() -> {
                        val chosenIndexes = input.split(' ')
                                .map { it.toInt() - 1 }

                        if (chosenIndexes.size > 2) {
                            tornadofx.error("You can't choose more than 2 definitions! (Primary and secondary ones)")
                            return
                        }

                        if (chosenIndexes.any { it >= allDefinitions.size }) {
                            tornadofx.error("Please check your selected indexes! You can choose from 1 to ${allDefinitions.size}!")
                            return
                        }

                        chosenDefinitions.addAll(chosenIndexes)
                        areDefinitionsSet = chosenDefinitions.size == 2
                    }

                    else -> {
                        allDefinitions.add(input)
                        chosenDefinitions.add(allDefinitions.lastIndex)
                        areDefinitionsSet = chosenDefinitions.size == 2
                    }
                }

                if (areDefinitionsSet) {
                    clFragment.label.text = "EXP"
                }

                return
            }

            if (!areExamplesSet) {
                when {
                    input.isEmpty() -> {
                        areExamplesSet = true
                    }

                    input.first().isDigit() -> {
                        val chosenIndexes = input.split(' ')
                                .map { it.toInt() - 1 }

                        if (chosenIndexes.any { it >= allExamples.size }) {
                            tornadofx.error("Please check your selected indexes! You can choose from 1 to ${allExamples.size}!")
                            return
                        }

                        chosenExamples.addAll(chosenIndexes)
                    }

                    else -> {
                        allExamples.add(input)
                        chosenExamples.add(allExamples.lastIndex)
                        return
                    }
                }

                resolutionUI.value = WordBlock.WordBlockResolution.UPDATED
            }
        }
    }

    internal class WordBlockHelp : View("Update Tab Info") {
        override val root: Parent = webview {
            prefWidth = 1000.0

            engine.load(resources["/WordBlock-help.html"])

            widthProperty().onChange {
                println(it)
            }
        }
    }

    internal class DistinctArrayList<E>(c: Collection<E>) : ArrayList<E>(c) {
        override fun add(index: Int, element: E) {
            if (contains(element)) {
                throw IllegalArgumentException("You can't add already present value!")
            }

            super.add(index, element)
        }
    }
}
