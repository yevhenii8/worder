package worder.model.insert.implementations

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import worder.model.BareWord
import worder.model.SharedStats.SharedStatsBinder
import worder.model.database.WorderInsertDB
import worder.model.database.WorderInsertDB.ResolveRes.INSERTED
import worder.model.insert.InsertBatch
import worder.model.insert.InsertBatch.InsertBatchStatus
import worder.model.insert.InsertBatch.InsertBatchStatus.COMMITTED
import worder.model.insert.InsertBatch.InsertBatchStatus.COMMITTING
import worder.model.insert.InsertBatch.InsertBatchStatus.PARTIALLY_COMMITTED
import worder.model.insert.InsertModel
import worder.model.insert.InsertUnit
import worder.model.insert.InsertUnit.InsertUnitStatus
import worder.model.insert.InsertUnit.InsertUnitStatus.EXCLUDED_FROM_BATCH
import worder.model.insert.InsertUnit.InsertUnitStatus.READY_TO_COMMIT
import worder.model.insert.InvalidWord
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.CoroutineContext

class BaseInsertModel private constructor(private val database: WorderInsertDB) : InsertModel {
    companion object {
        private val bulk = "[^A-Za-z\\-' ]".toRegex()
        private val wordValidator: (word: String) -> Boolean = { word -> !word.isBlank() && !word.contains(bulk) }

        fun createInstance(database: WorderInsertDB): InsertModel = BaseInsertModel(database)
    }


    override fun prepareBatch(files: List<File>): InsertBatch {
        if (files.isEmpty())
            throw IllegalArgumentException("Please provide at least one file!")

        files.forEach {
            if (!(it.isFile && it.canRead()))
                throw IllegalArgumentException("Please provide correct readable file!")
        }

        return BaseInsertBatch(files)
    }


    /*
    BATCH INNER CLASS STARTED
     */
    private inner class BaseInsertBatch(files: List<File>) : InsertBatch, CoroutineScope {
        override val id: String = LocalDateTime.now().withNano(0).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        override val units: MutableList<InsertUnit> = files.mapIndexed { index, file -> BaseInsertUnit("${id}_$index", file) }.toMutableList()
        override var batchStats: BaseInsertBatchStats = BaseInsertBatchStats(id = id)
        override var status: InsertBatchStatus by SharedStatsBinder.bind(batchStats, InsertBatchStatus.READY_TO_COMMIT)
        override val coroutineContext: CoroutineContext = Dispatchers.Default


        override suspend fun commitAll() {
            if (status == COMMITTED)
                throw IllegalStateException("Batch can be committed only once!")

            status = COMMITTING
            supervisorScope {
                units.filter { it.isIncluded }.forEach { launch { it.commit() } }
            }
            status = COMMITTED
        }

        override fun toString(): String = "InsertBatch(id=$id, status=$status, units=${units.size})"


        /*
        UNIT INNER CLASS STARTED
         */
        private inner class BaseInsertUnit(override val id: String, file: File) : InsertUnit {
            override val invalidWords: MutableSet<InvalidWord> = file.readLines().filterNot(wordValidator).map { BaseInvalidWord(it) }.toMutableSet()
            override val validWords: MutableSet<BareWord> = file.readLines().filter(wordValidator).map { BareWord(it) }.toMutableSet()
            override var isIncluded: Boolean = invalidWords.isEmpty()
            override val unitStats = BaseInsertUnitStats(
                    id = id,
                    fileName = file.name,
                    fileSize = file.length(),
                    invalidWords = invalidWords.size,
                    validWords = validWords.size
            )

            override var status: InsertUnitStatus by SharedStatsBinder.bind(unitStats, READY_TO_COMMIT)


            override suspend fun commit() {
                if (status == InsertUnitStatus.COMMITTED)
                    throw IllegalStateException("Unit can be committed only once!")

                if (isIncluded) {
                    status = InsertUnitStatus.COMMITTING

                    val res = validWords
                            .shuffled()
                            .map { database.resolveWord(it) }
                            .partition { it == INSERTED }

                    batchStats.apply {
                        reset += res.first.size
                        inserted += res.second.size

                        validProcessed += unitStats.validWords
                        invalidProcessed += unitStats.invalidWords

                        totalProcessed = validProcessed + invalidProcessed
                    }
                }

                batchStats.committedUnits++
                status = InsertUnitStatus.COMMITTED

                if (this@BaseInsertBatch.status != COMMITTING)
                    this@BaseInsertBatch.status = PARTIALLY_COMMITTED
            }

            override fun excludeFromBatch() {
                if (status != InsertUnitStatus.COMMITTED) {
                    isIncluded = false
                    status = EXCLUDED_FROM_BATCH
                }
            }

            override fun includeInBatch() {
                if (status != InsertUnitStatus.COMMITTED) {
                    isIncluded = true
                    status = READY_TO_COMMIT
                }
            }

            override fun toString(): String = "InsertUnit(id=$id, status=$status, fileName=${unitStats.fileName})"


            /*
            INVALID WORD INNER CLASS STARTED
             */
            inner class BaseInvalidWord(override val value: String) : InvalidWord {
                override fun reject() {
                    invalidWords.remove(this)
                    isIncluded = invalidWords.isEmpty()
                }

                override fun substitute(substitution: String): Boolean {
                    return if (wordValidator.invoke(substitution)) {
                        validWords.add(BareWord(substitution))
                        isIncluded = invalidWords.isEmpty()
                        true
                    } else {
                        false
                    }
                }
            }
        }
    }
}
