package worder.model.extract.update

import worder.*
import worder.model.database.WorderUpdateDB
import worder.model.extract.request.*
import worder.model.extract.request.implementations.*
import java.util.*


class UpdateModel(
        val pipelineLength: Int,
        val database: WorderUpdateDB,
        var selectOrder: WorderUpdateDB.SelectOrder
) {
    private val definitionRequesters = mutableSetOf<DefinitionRequester>()
    private val exampleRequesters = mutableSetOf<ExampleRequester>()
    private val translationRequesters = mutableSetOf<TranslationRequester>()
    private val transcriptionRequesters = mutableSetOf<TranscriptionRequester>()

    private val pipeline = WordBlockPipeline()
//    private var lastBlock: WordBlock? = null
//    private var hasNext = false


    init {
        setOf(Cambridge.instance, Lingvo.instance, Macmillan.instance, WooordHunt.instance)
            .forEach {
                if (it is DefinitionRequester) definitionRequesters += it
                if (it is TranslationRequester) translationRequesters += it
                if (it is ExampleRequester) exampleRequesters += it
                if (it is TranscriptionRequester) transcriptionRequesters += it
            }
    }


    private inner class WordBlockPipeline {
        private val readyToCommit: Queue<BaseWordBlock> = ArrayDeque<BaseWordBlock>()

        private var waitingForClientResponse: BaseWordBlock? = null
        private var readyForClientDelivery: BaseWordBlock? = null


        init {
        }


        fun commitAll() {}

        fun hasNext(): Boolean = waitingForClientResponse == null && readyForClientDelivery != null

        private fun getNextWordBlock() : BaseWordBlock? {
            TODO()
        }
    }

    private inner class BaseWordBlock(
        override val dbWord: DatabaseWord,
        override val serialNumber: Int,

        override val translations: Set<String>,
        override val examples: Set<String>,
        override val definitions: Set<String>,
        override val transcriptions: Set<String>
    ) : WordBlock {
        var command: BlockCommand? = null
            private set(value) {
                if (isCommitted)
                    throw IllegalStateException("This WordBlock has already been committed!")

                if (field == null) {
//                    field = value
//                    readyToCommit.add(this)
//                    database.setSkipped(dbWord)
//
//                    if (readyToCommit.size > pipelineLength)
//                        readyToCommit.remove().command!!.commit()
//
//                    lastBlock = null
                }

                field = value
            }

        var isCommitted = false
            private set
        override val resolution: String
            get() = command.toString()

        override lateinit var status: worder.model.extract.update.WordBlock.BlockStatus


        private fun setResolution(blockCommand: BlockCommand): Boolean {
            if (isCommitted)
                return false

            command = blockCommand
            return true
        }

        override fun skip(): Boolean = setResolution(SkippedCommand())
        override fun remove(): Boolean = setResolution(RemoveCommand())
        override fun learn(): Boolean = setResolution(LearnedCommand())
        override fun update(primaryDefinition: String, secondaryDefinition: String?, examples: Set<String>, transcription: String?): Boolean =
            setResolution(
                UpdateCommand(
                    BaseUpdatedWord(
                        name = dbWord.name,
                        transcription = transcription,
                        primaryDefinition = primaryDefinition,
                        secondaryDefinition = secondaryDefinition,
                        examples = examples
                    )
                )
            )


        abstract inner class BlockCommand {
            fun commit() {
                isCommitted = true
                execute()
            }

            abstract fun execute()
            abstract override fun toString(): String
        }

        inner class RemoveCommand : BlockCommand() {
            override fun execute() {
                database.removeWord(dbWord)
            }

            override fun toString(): String = "REMOVED"
        }

        inner class SkippedCommand : BlockCommand() {
            override fun execute() {}
            override fun toString(): String = "SKIPPED"
        }

        inner class LearnedCommand : BlockCommand() {
            override fun execute() {
                database.setLearned(dbWord)
            }

            override fun toString(): String = "LEARNED"
        }

        inner class UpdateCommand(private val updatedWord: UpdatedWord) : BlockCommand() {
            override fun execute() {
                database.updateWord(updatedWord)
            }

            override fun toString(): String = "UPDATED"
        }
    }


    fun iterator(): Iterator<WordBlock> = TODO() //this

    fun hasNext(): Boolean {
//        hasNext = database.hasNextWord() && lastBlock == null
//        return hasNext
        TODO()
    }

    fun next(): WordBlock {
//        if (!hasNext)
//            throw IllegalStateException("hasNext() returned false or wasn't called at all!")
//
//        val dbWord = database.getNextWord(selectOrder)
//        //requesters.forEach { it.requestWord(dbWord) }
//
//        val definitions = definitionRequesters.flatMap { it.definitions }.toSet()
//        val examples = (exampleRequesters.flatMap { it.examples } + dbWord.examples).toSet()
//        val translations = (translationRequesters.flatMap { it.translations } + dbWord.translations).toSet()
//        val transcriptions = (transcriptionRequesters.flatMap { it.transcriptions } + setOf(dbWord.transcription)).filterNotNull().toSet()
//
//        lastBlock = BaseWordBlock(
//            dbWord = dbWord,
//            serialNumber = readyToCommit.size + 1,
//            translations = translations,
//            examples = examples,
//            definitions = definitions,
//            transcriptions = transcriptions
//        )
//        hasNext = false
//
//        return lastBlock!!
        TODO()
    }

    fun exit(): Unit = TODO() //readyToCommit.forEach { it.command!!.commit() }
}
