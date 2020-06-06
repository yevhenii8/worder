package worder.model.insert.implementations

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
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


    private inner class BaseInsertBatch(files: List<File>) : InsertBatch, CoroutineScope {
        override val id: String = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
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


        private inner class BaseInsertUnit(override val id: String, file: File) : InsertUnit {
            override var isIncluded: Boolean = true
            override val invalidWords: Set<String> = file.readLines().filterNot(wordValidator).toSet()
            override val validWords: Set<String> = file.readLines().filter(wordValidator).toSet()
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
        }
    }
}
