package worder.model.insert.implementations

import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.ReadOnlyProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import tornadofx.getValue
import tornadofx.observableListOf
import tornadofx.setValue
import tornadofx.toObservable
import worder.model.BareWord
import worder.model.SharedStats.SharedStatsBinder
import worder.model.database.WorderInsertDB
import worder.model.insert.InsertModel
import worder.model.insert.InsertModel.InsertModelStatus
import worder.model.insert.InsertUnit
import worder.model.insert.InsertUnit.InsertUnitStatus
import worder.model.insert.InsertUnit.InvalidWord
import java.io.File

class SimpleInsertModel private constructor(private val database: WorderInsertDB) : InsertModel {
    companion object {
        fun createInstance(database: WorderInsertDB): InsertModel = SimpleInsertModel(database)
    }


    private var unitsCounter = 0

    override val statusProperty: ObjectProperty<InsertModelStatus> = SimpleObjectProperty(InsertModelStatus.CREATED)
    override var status: InsertModelStatus by statusProperty

    override val stats: BaseInsertModelStats = BaseInsertModelStats()

    override val uncommittedUnitsProperty: ListProperty<InsertUnit> = SimpleListProperty(observableListOf())
    override val uncommittedUnits: MutableList<InsertUnit> by uncommittedUnitsProperty


    override fun generateUnits(files: List<File>): List<InsertUnit> {
        files.forEach {
            if (!(it.isFile && it.canRead()))
                throw IllegalArgumentException("Please provide correct readable file!")
        }

        val newUnits = files.map { file ->
            val (validWords, invalidWords) = file.readLines()
                    .distinct()
                    .map { it.trim() }
                    .partition { BareWord.wordValidator.invoke(it) }

            stats.apply {
                totalValidWords += validWords.size
                totalInvalidWords += invalidWords.size
            }

            BaseInsertUnit(
                    id = "Unit_${++unitsCounter}",
                    source = file.name,
                    validWords = validWords,
                    invalidWords = invalidWords
            )
        }

        uncommittedUnits.addAll(newUnits)
        stats.generatedUnits += newUnits.size

        return newUnits
    }

    override suspend fun commitAllUnits() {
        supervisorScope {
            uncommittedUnits.forEach { launch { it.commit() } }
        }
    }


    private inner class BaseInsertUnit(
            id: String,
            source: String,
            validWords: List<String>,
            invalidWords: List<String>
    ) : InsertUnit {
        private var state: UnitState

        override val idProperty: ReadOnlyStringProperty = SimpleStringProperty(id)
        override val id: String by idProperty

        override val statusProperty: ObjectProperty<InsertUnitStatus> = SimpleObjectProperty()
        override var status: InsertUnitStatus by statusProperty

        override val sourceProperty: ReadOnlyStringProperty = SimpleStringProperty(source)
        override val source: String by sourceProperty

        override val validWordsProperty: ListProperty<BareWord> = SimpleListProperty()
        override val validWords: MutableList<BareWord> by validWordsProperty

        override val invalidWordsProperty: ListProperty<InvalidWord> = SimpleListProperty()
        override val invalidWords: MutableList<InvalidWord> by invalidWordsProperty


        init {
            validWordsProperty.set(validWords.map { BareWord(it) }.toObservable())
            invalidWordsProperty.set(invalidWords.map { BaseInvalidWord(it) }.toObservable())
            state = if (invalidWords.isEmpty()) ReadyToCommitState() else ActionNeededState()
        }


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


        /*
        Actually It's a finite-state machine
         */
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
                this@SimpleInsertModel.status = InsertModelStatus.READY_TO_COMMIT
            }

            override suspend fun commit() {
                state = CommittingState()

                val (reset, inserted) = validWords
                        .map { database.resolveWord(it) }
                        .partition { it == WorderInsertDB.ResolveRes.RESET }

                this@SimpleInsertModel.stats.apply {
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
                this@SimpleInsertModel.apply {
                    uncommittedUnits.remove(this@BaseInsertUnit)
                    status = if (uncommittedUnits.isEmpty())
                        InsertModelStatus.COMMITTED
                    else
                        InsertModelStatus.PARTIALLY_COMMITTED
                }
            }
        }
    }
}
