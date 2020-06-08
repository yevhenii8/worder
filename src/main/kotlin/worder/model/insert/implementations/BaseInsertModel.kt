package worder.model.insert.implementations

import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import worder.model.BareWord
import worder.model.SharedStats.SharedStatsBinder
import worder.model.database.WorderInsertDB
import worder.model.insert.InsertModel
import worder.model.insert.InsertModel.InsertModelStatus
import worder.model.insert.InsertUnit
import worder.model.insert.InsertUnit.InsertUnitStatus
import worder.model.insert.InsertUnit.InvalidWord
import java.io.File

class BaseInsertModel private constructor(private val database: WorderInsertDB) : InsertModel {
    companion object {
        fun createInstance(database: WorderInsertDB): InsertModel = BaseInsertModel(database)
    }


    private var unitsCounter = 0

    override val stats: BaseInsertModelStats = BaseInsertModelStats()
    override var status: InsertModelStatus by SharedStatsBinder.bind(stats, InsertModelStatus.CREATED)
    override val uncommittedUnits: MutableList<InsertUnit> = mutableListOf()


    override fun generateUnits(files: List<File>): List<InsertUnit> {
        files.forEach {
            if (!(it.isFile && it.canRead()))
                throw IllegalArgumentException("Please provide correct readable file!")
        }

        val newUnits = files.map { BaseInsertUnit(it.name, it.readLines()) }
        uncommittedUnits.addAll(newUnits)
        stats.generatedUnits += newUnits.size

        return newUnits
    }

    override suspend fun commitAllUnits() {
        supervisorScope {
            uncommittedUnits.forEach { launch { it.commit() } }
        }
    }


    private inner class BaseInsertUnit(source: String, words: List<String>) : InsertUnit {
        private var state: UnitState

        override val id: String = "Unit_${++unitsCounter}"
        override val invalidWords: MutableSet<InvalidWord>
        override val validWords: MutableSet<BareWord>


        init {
            val (valid, invalid) = words.map { it.trim() }.partition { BareWord.wordValidator.invoke(it) }

            validWords = valid.map { BareWord(it) }.toMutableSet()
            invalidWords = invalid.map { BaseInvalidWord(it) }.toMutableSet()

            this@BaseInsertModel.stats.apply {
                totalValidWords += valid.size
                totalInvalidWords += invalid.size
            }

            state = if (invalidWords.isEmpty()) ReadyToCommitState() else ActionNeededState()
        }


        override val stats: BaseInsertUnitStats = BaseInsertUnitStats(
                id = id,
                source = source,
                invalidWords = invalidWords.size,
                validWords = validWords.size
        )
        override var status: InsertUnitStatus by SharedStatsBinder.bind(stats, null)


        override suspend fun commit() = state.commit()
        override fun excludeFromCommit() = state.excludeFromCommit()
        override fun includeInCommit() = state.includeInCommit()


        private inner class BaseInvalidWord(override val value: String) : InvalidWord {
            private fun updateState() {
                if (invalidWords.isEmpty() && status == InsertUnitStatus.ACTION_NEEDED)
                    state = ReadyToCommitState()
            }

            override fun reject() {
                invalidWords.remove(this)
                updateState()
            }

            override fun substitute(substitution: String): Boolean {
                return try {
                    validWords.add(BareWord(substitution))
                    invalidWords.remove(this)
                    updateState()
                    true
                } catch (e: IllegalArgumentException) {
                    false
                }
            }
        }


        private abstract inner class UnitState(status: InsertUnitStatus) {
            init {
                this@BaseInsertUnit.status = status
            }

            open suspend fun commit() {
                throw IllegalStateException("You can't commit unit with status: $status")
            }

            open fun excludeFromCommit() {
                throw IllegalStateException("You can't exclude unit with status: $status")
            }

            open fun includeInCommit() {
                throw IllegalStateException("You can't include unit with status: $status")
            }
        }

        private inner class ReadyToCommitState : UnitState(InsertUnitStatus.READY_TO_COMMIT) {
            init {
                this@BaseInsertModel.status = InsertModelStatus.READY_TO_COMMIT
            }

            override suspend fun commit() {
                state = CommittingState()

                val (reset, inserted) = validWords
                        .map { database.resolveWord(it) }
                        .partition { it == WorderInsertDB.ResolveRes.RESET }

                this@BaseInsertModel.stats.apply {
                    committedUnits++
                    this.reset += reset.size
                    this.inserted += inserted.size
                }

                state = CommittedState()
            }

            override fun excludeFromCommit() {
                state = ExcludedFromCommitState()
            }
        }

        private inner class ActionNeededState : UnitState(InsertUnitStatus.ACTION_NEEDED) {
            override fun excludeFromCommit() {
                state = ExcludedFromCommitState()
            }
        }

        private inner class ExcludedFromCommitState : UnitState(InsertUnitStatus.EXCLUDED_FROM_COMMIT) {
            override fun includeInCommit() {
                state = ReadyToCommitState()
            }
        }

        private inner class CommittingState : UnitState(InsertUnitStatus.COMMITTING)

        private inner class CommittedState : UnitState(InsertUnitStatus.COMMITTED) {
            init {
                this@BaseInsertModel.status =
                        if (uncommittedUnits.isEmpty()) InsertModelStatus.COMMITTED else InsertModelStatus.PARTIALLY_COMMITTED
            }
        }
    }
}
