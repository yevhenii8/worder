package worder.model.insert.implementations

import worder.model.database.WorderInsertDB
import worder.model.insert.InsertBatch
import worder.model.insert.InsertBatchStats.InsertBatchStatus
import worder.model.insert.InsertModel
import worder.model.insert.InsertUnit
import worder.model.insert.InsertUnitStats
import worder.model.insert.InsertUnitStats.InsertUnitStatus
import java.io.File
import java.time.LocalDateTime

class BaseInsertModel(private val database: WorderInsertDB) : InsertModel {
    companion object {
        private val bulk = "[^A-Za-z\\-' ]".toRegex()
        private val wordValidator: (word: String) -> Boolean = { word -> !word.isBlank() && !word.contains(bulk) }
    }


    override fun prepareBatch(files: List<File>): InsertBatch {
        files.forEach {
            if (!(it.isFile && it.canRead()))
                throw IllegalArgumentException("Please provide correct readable file!")
        }

        return BaseInsertBatch(files)
    }


    private inner class BaseInsertBatch(files: List<File>) : InsertBatch {
        private val creationTime = LocalDateTime.now()

        override val units: List<InsertUnit> = files.map { BaseInsertUnit(it) }
        override var batchStats: BaseInsertBatchStats = BaseInsertBatchStats(
                origin = toString(),
                batchStatus = InsertBatchStatus.READY_TO_PROCESS,
                totalProcessed = 0,
                validProcessed = 0,
                invalidProcessed = 0,
                reset = 0,
                inserted = 0,
                spentTime = "0.0",
                progressBar = "0/${units.size}"
        )


        override suspend fun processBatch() {
            TODO("Not yet implemented")
        }


        override fun toString(): String = "InsertBatch $creationTime"


        private inner class BaseInsertUnit(file: File) : InsertUnit {
            override val invalidWords: Set<String> = file.readLines().filterNot(wordValidator).toSet()
            override val validWords: Set<String> = file.readLines().filter(wordValidator).toSet()
            override val insertUnitStats: InsertUnitStats = BaseInsertUnitStats(
                    origin = toString(),
                    fileName = file.name,
                    fileSize = file.length(),
                    status = InsertUnitStatus.READY_TO_PROCESS
            )


            override fun excludeFromBatch() {
                TODO("Not yet implemented")
            }

            override fun includeInBatch() {
                TODO("Not yet implemented")
            }

            override suspend fun process() {
                TODO("Not yet implemented")
            }


            override fun toString(): String = "InsertUnit (${this@BaseInsertBatch})"
        }
    }
}
