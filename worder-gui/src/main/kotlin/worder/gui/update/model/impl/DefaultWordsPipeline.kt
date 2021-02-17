/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DefaultWordsPipeline.kt>
 * Created: <20/07/2020, 11:22:35 PM>
 * Modified: <17/02/2021, 05:18:47 PM>
 * Version: <105>
 */

package worder.gui.update.model.impl

import javafx.beans.property.ListProperty
import javafx.beans.property.Property
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.onChangeOnce
import tornadofx.setValue
import worder.gui.database.model.DatabaseWord
import worder.gui.database.model.UpdatedWord
import worder.gui.database.model.WorderUpdateDB
import worder.gui.update.model.DefinitionRequester
import worder.gui.update.model.ExampleRequester
import worder.gui.update.model.Requester
import worder.gui.update.model.TranscriptionRequester
import worder.gui.update.model.TranslationRequester
import worder.gui.update.model.WordBlock
import worder.gui.update.model.WordsPipeline

class DefaultWordsPipeline private constructor(
        val database: WorderUpdateDB,
        override val usedRequesters: List<Requester>,
        override var selectOrder: WorderUpdateDB.SelectOrder
) : WordsPipeline {
    companion object {
        fun createInstance(
                database: WorderUpdateDB,
                usedRequesters: List<Requester>,
                selectOrder: WorderUpdateDB.SelectOrder
        ): WordsPipeline = DefaultWordsPipeline(database, usedRequesters, selectOrder)
    }


    private val definitionRequesters = mutableSetOf<DefinitionRequester>()
    private val exampleRequesters = mutableSetOf<ExampleRequester>()
    private val translationRequesters = mutableSetOf<TranslationRequester>()
    private val transcriptionRequesters = mutableSetOf<TranscriptionRequester>()

    override val pipelineProperty: ListProperty<WordBlock> = SimpleListProperty(observableListOf())
    override val pipeline: MutableList<WordBlock> by pipelineProperty

    override val isConsumedProperty: Property<Boolean> = SimpleObjectProperty<Boolean>(false)
    override var isConsumed: Boolean by isConsumedProperty

    override val isCommittedProperty: Property<Boolean> = SimpleObjectProperty<Boolean>(false)
    override var isCommitted: Boolean by isCommittedProperty

    private var isEmptyInternal: Boolean = false
    private lateinit var backgroundJob: Job
    private lateinit var current: WordBlock
    private var readyToCommit: WordBlock? = null
    private var next: WordBlock? = null
    private var blocksCounter = 1


    init {
        usedRequesters.forEach {
            if (it is DefinitionRequester)
                definitionRequesters += it
            if (it is TranslationRequester)
                translationRequesters += it
            if (it is ExampleRequester)
                exampleRequesters += it
            if (it is TranscriptionRequester)
                transcriptionRequesters += it
        }

        CoroutineScope(Dispatchers.Default).launch {
            val firstBlock = composeNext()

            if (firstBlock == null) {
                MainScope().launch {
                    isConsumed = true
                }
                return@launch
            }

            current = firstBlock
            backgroundJob = launch {
                next = composeNext()
            }

            MainScope().launch {
                pipeline.add(current)
            }
        }
    }


    private suspend fun composeNext(): WordBlock? {
        if (!database.hasNextWord()) {
            isEmptyInternal = true
            return null
        }

//        val dbWord = DatabaseWord("sclerosis", null, 0, 0, 0, 0, 0, emptyList(), emptyList())
        val dbWord = database.getNextWord(selectOrder)
        usedRequesters.forEach { it.requestWord(dbWord) }

        val definitions = definitionRequesters.flatMap { it.definitions }.distinct()
        val examples = (exampleRequesters.flatMap { it.examples } + dbWord.examples).distinct()
        val translations = (translationRequesters.flatMap { it.translations } + dbWord.translations).distinct()
        val transcriptions = (listOf(dbWord.transcription) + transcriptionRequesters.flatMap { it.transcriptions }).filterNotNull().distinct()

        val newBlock = DefaultWordBlock(
                id = "${blocksCounter++}",
                originalWord = dbWord,
                definitions = definitions,
                examples = examples,
                translations = translations,
                transcriptions = transcriptions
        )

        newBlock.apply {
            statusProperty.onChangeOnce {
                CoroutineScope(Dispatchers.Default).launch {
                    backgroundJob.join()

                    if (isEmptyInternal) {
                        backgroundJob = launch {
                            readyToCommit?.commit()
                            MainScope().launch {
                                isConsumed = true
                            }
                        }
                        return@launch
                    }

                    backgroundJob = launch {
                        readyToCommit?.commit()
                        readyToCommit = current
                        current = next!!

                        MainScope().launch {
                            pipeline.add(current)
                        }

                        next = composeNext()
                    }
                }
            }
        }

        return newBlock
    }


    private inner class DefaultWordBlock(
            override val id: String,
            override val originalWord: DatabaseWord,
            override val definitions: List<String>,
            override val examples: List<String>,
            override val translations: List<String>,
            override val transcriptions: List<String>
    ) : WordBlock {
        override val statusProperty: Property<WordBlock.WordBlockStatus> = SimpleObjectProperty(WordBlock.WordBlockStatus.RESOLUTION_NEEDED)
        override var status: WordBlock.WordBlockStatus by statusProperty

        override val resolutionProperty: Property<WordBlock.WordBlockResolution> = SimpleObjectProperty(WordBlock.WordBlockResolution.NO_RESOLUTION)
        override var resolution: WordBlock.WordBlockResolution by resolutionProperty

        private var updatedWord: UpdatedWord? = null


        override suspend fun commit() {
            if (status == WordBlock.WordBlockStatus.COMMITTED)
                return

            if (status != WordBlock.WordBlockStatus.READY_TO_COMMIT)
                error("You can't commit block with status: $status")

            when (resolution) {
                WordBlock.WordBlockResolution.SKIPPED -> database.setAsSkipped(originalWord)
                WordBlock.WordBlockResolution.REMOVED -> database.removeWord(originalWord)
                WordBlock.WordBlockResolution.LEARNED -> database.setAsLearned(originalWord)
                WordBlock.WordBlockResolution.UPDATED -> database.updateWord(updatedWord!!)
                WordBlock.WordBlockResolution.NO_RESOLUTION -> error("You can't commit block with NO_RESOLUTION!")
            }


            MainScope().launch {
                status = WordBlock.WordBlockStatus.COMMITTED
                if (pipeline.last() == this@DefaultWordBlock)
                    isCommitted = true
            }
        }

        override fun update(primaryDefinition: String, secondaryDefinition: String?, transcription: String?, examples: List<String>) {
            if (status == WordBlock.WordBlockStatus.COMMITTED)
                error("You can't update block with status: $status")

            updatedWord = UpdatedWord(
                name = originalWord.name,
                transcription = transcription,
                primaryDefinition = primaryDefinition,
                secondaryDefinition = secondaryDefinition,
                examples = examples
            )

            resolution = WordBlock.WordBlockResolution.UPDATED
            status = WordBlock.WordBlockStatus.READY_TO_COMMIT
        }

        override fun remove() {
            if (status == WordBlock.WordBlockStatus.COMMITTED)
                error("You can't remove block with status: $status")

            resolution = WordBlock.WordBlockResolution.REMOVED
            status = WordBlock.WordBlockStatus.READY_TO_COMMIT
        }

        override fun learn() {
            if (status == WordBlock.WordBlockStatus.COMMITTED)
                error("You can't learn block with status: $status")

            resolution = WordBlock.WordBlockResolution.LEARNED
            status = WordBlock.WordBlockStatus.READY_TO_COMMIT
        }

        override fun skip() {
            if (status == WordBlock.WordBlockStatus.COMMITTED)
                error("You can't skip block with status: $status")

            resolution = WordBlock.WordBlockResolution.SKIPPED
            status = WordBlock.WordBlockStatus.READY_TO_COMMIT
        }
    }
}
