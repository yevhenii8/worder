/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DefaultUpdateModel.kt>
 * Created: <20/07/2020, 11:22:35 PM>
 * Modified: <22/07/2020, 12:04:19 AM>
 * Version: <4>
 */

package worder.update.model.impl

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import worder.database.model.WorderUpdateDB
import worder.update.model.DefinitionRequester
import worder.update.model.ExampleRequester
import worder.update.model.Requester
import worder.update.model.TranscriptionRequester
import worder.update.model.TranslationRequester
import worder.update.model.UpdateModel
import worder.update.model.WordBlock

class DefaultUpdateModel private constructor(
        val database: WorderUpdateDB,
        override val requesters: List<Requester>,
        override var selectOrder: WorderUpdateDB.SelectOrder = WorderUpdateDB.SelectOrder.RANDOM
) : UpdateModel, Iterator<WordBlock> {
    private val definitionRequesters = mutableSetOf<DefinitionRequester>()
    private val exampleRequesters = mutableSetOf<ExampleRequester>()
    private val translationRequesters = mutableSetOf<TranslationRequester>()
    private val transcriptionRequesters = mutableSetOf<TranscriptionRequester>()


    init {
        requesters.forEach {
            if (it is DefinitionRequester)
                definitionRequesters += it
            if (it is TranslationRequester)
                translationRequesters += it
            if (it is ExampleRequester)
                exampleRequesters += it
            if (it is TranscriptionRequester)
                transcriptionRequesters += it
        }
    }


    override val uncommittedBlocks: MutableList<WordBlock> = mutableListOf()
    private var next: WordBlock? = runBlocking { composeNext() }


    private suspend fun composeNext(): WordBlock? {
        if (!database.hasNextWord())
            return null

        val dbWord = database.getNextWord(selectOrder)
        requesters.forEach { it.requestWord(dbWord) }

        val definitions = definitionRequesters.flatMap { it.definitions }.distinct()
        val examples = (exampleRequesters.flatMap { it.examples } + dbWord.examples).distinct()
        val translations = (translationRequesters.flatMap { it.translations } + dbWord.translations).distinct()
        val transcriptions = (transcriptionRequesters.flatMap { it.transcriptions } + setOf(dbWord.transcription)).filterNotNull().distinct()

//        lastBlock = BaseWordBlock(
//                dbWord = dbWord,
//                serialNumber = readyToCommit.size + 1,
//                translations = translations,
//                examples = examples,
//                definitions = definitions,
//                transcriptions = transcriptions
//        )
//        hasNext = false
//
//        return lastBlock!!

        return null
    }


    override fun iterator(): Iterator<WordBlock> = this

    override fun next(): WordBlock {
        TODO("Not yet implemented")
    }

    override fun hasNext(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun commitAllBlocks() {
        coroutineScope {
            ArrayList(uncommittedBlocks).apply {
                filter { it.status == WordBlock.WordBlockStatus.READY_TO_COMMIT }
                        .forEach { launch { it.commit() } }
            }
        }
    }
}
