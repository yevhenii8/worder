package worder.model

import worder.database.WordsUpdateDB
import worder.request.*
import java.util.*
import kotlin.IllegalStateException
import kotlin.reflect.full.createInstance


class UpdateModel(
    val database: WordsUpdateDB,
    var selectOrder: WordsUpdateDB.SelectOrder,
    pipelineLength: Int,
    useDefaultRequesters: Boolean = true,
    requesters: Set<Requester> = emptySet()
) : Iterable<UpdateModel.WordBlock>, Iterator<UpdateModel.WordBlock> {
    private val definitionRequesters = mutableSetOf<DefinitionRequester>()
    private val exampleRequesters = mutableSetOf<ExampleRequester>()
    private val translationRequesters = mutableSetOf<TranslationRequester>()
    private val transcriptionRequesters = mutableSetOf<TranscriptionRequester>()
    private val uncommittedBlocks: Queue<WordBlock> = ArrayDeque<WordBlock>(pipelineLength)

    private var pipelineLength = pipelineLength + 1
    private var lastBlock: WordBlock? = null

    val requesters: Set<Requester>


    init {
        val temporaryRequesters = mutableSetOf<Requester>()

        if (useDefaultRequesters)
            temporaryRequesters.addAll(Requester.getAllDefaultImplementations())
        temporaryRequesters.addAll(requesters)

        this.requesters = temporaryRequesters.toSet()
        requesters.forEach {
            if (it is DefinitionRequester) definitionRequesters += it
            if (it is TranslationRequester) translationRequesters += it
            if (it is ExampleRequester) exampleRequesters += it
            if (it is TranscriptionRequester) transcriptionRequesters += it
        }

    }


    inner class WordBlock private constructor(
        dbWord: DatabaseWord,

        override val translations: Set<String>,
        override val examples: Set<String>,

        val definitions: Set<String>,
        val transcriptions: Set<String>,

        val serialNumber: Int
    ) : DatabaseWord by dbWord {
        private var isCommitted = false
        var resolution: BlockCommand? = null
            set(value) {
                if (isCommitted)
                    throw IllegalStateException("This WordBlock has already been committed!")

                if (field == null) {
                    lastBlock = null
                    addToPipeline(this)
                }

                field = value
            }


        abstract inner class BlockCommand {
            fun commit() {
                isCommitted = true
                execute()
            }

            abstract fun execute()
        }
        inner class RemoveCommand : BlockCommand() {
            override fun execute() {
                database.removeWord(this@WordBlock)
            }
        }
        inner class SkippedCommand : BlockCommand() {
            override fun execute() { }
        }
        inner class LearnedCommand : BlockCommand() {
            override fun execute() {
                database.setLearned(this@WordBlock)
            }
        }
        inner class UpdateCommand(private val updatedWord: UpdatedWord) : BlockCommand() {
            override fun execute() {
                database.updateWord(updatedWord)
            }
        }
    }


    override fun iterator(): Iterator<WordBlock> = this

    override fun hasNext(): Boolean = database.hasNextWord() && lastBlock == null

    override fun next(): WordBlock {
        val dbWord = database.getNextWord(selectOrder)
        requesters.forEach { it.acceptWord(dbWord) }

        val definitions = definitionRequesters.flatMap { it.getDefinitions() }.toSet()
        val examples = (exampleRequesters.flatMap { it.getExamples() } + dbWord.examples).toSet()
        val translations = (translationRequesters.flatMap { it.getTranslations() } + dbWord.translations).toSet()
        val transcriptions = (transcriptionRequesters.flatMap { it.getTranscriptions() } + (dbWord.transcription ?: Nothing::class.createInstance())).toSet()

        lastBlock = WordBlock(dbWord, translations, examples, definitions, transcriptions, 0)

        return lastBlock!!
    }


    fun exit() = uncommittedBlocks.forEach { it.resolution!!.commit() }


    private fun addToPipeline(wordBlock: WordBlock) {
        if (uncommittedBlocks.size == pipelineLength)
            uncommittedBlocks.remove().resolution!!.commit()

        uncommittedBlocks.add(wordBlock)
        database.setSkipped(wordBlock)
    }
}
