package worder.model.insert.implementations

import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
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
import worder.model.database.WorderInsertDB
import worder.model.insert.InsertModel
import worder.model.insert.InsertModel.InsertModelStatus
import worder.model.insert.InsertUnit
import worder.model.insert.InsertUnit.InsertUnitStatus
import worder.model.insert.InsertUnit.InvalidWord
import java.io.File

class SimpleInsertModel private constructor(private val database: WorderInsertDB, files: List<File>) : InsertModel {
    companion object {
        fun createInstance(database: WorderInsertDB, files: List<File>): InsertModel = SimpleInsertModel(database, files)
    }


    private var unitsCounter = 0

    override val statusProperty: ObjectProperty<InsertModelStatus> = SimpleObjectProperty(InsertModelStatus.CREATED)
    override var status: InsertModelStatus by statusProperty

    override val uncommittedUnitsProperty: ListProperty<InsertUnit> = SimpleListProperty(observableListOf())
    override val uncommittedUnits: MutableList<InsertUnit> by uncommittedUnitsProperty

    override val stats: SimpleInsertModelStats = object : SimpleInsertModelStats() {
        override var uncommittedUnits: Int by bindToStats(uncommittedUnitsProperty.sizeProperty())
    }


    init {
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

            SimpleInsertUnit(
                    id = "Unit_${++unitsCounter}",
                    source = file.name,
                    validWords = validWords,
                    invalidWords = invalidWords
            )
        }

        uncommittedUnits.addAll(newUnits)
        stats.generatedUnits += newUnits.size
    }


    override suspend fun commitAllUnits() {
        supervisorScope {
            uncommittedUnits.forEach { launch { it.commit() } }
        }
    }


    private inner class SimpleInsertUnit(
            id: String,
            source: String,
            validWords: List<String>,
            invalidWords: List<String>
    ) : InsertUnit {
        private var stateController: StateController

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
            invalidWordsProperty.set(invalidWords.map { SimpleInvalidWord(it) }.toObservable())
            stateController = StateController(
                    if (invalidWords.isEmpty())
                        InsertUnitStatus.READY_TO_COMMIT
                    else
                        InsertUnitStatus.ACTION_NEEDED
            )
        }


        override suspend fun commit() = stateController.commit()
        override fun excludeFromCommit() = stateController.excludeFromCommit()
        override fun includeInCommit() = stateController.includeInCommit()


        private inner class SimpleInvalidWord(override val value: String) : InvalidWord {
            private fun updateState() {
//                if (invalidWords.isEmpty() && status == InsertUnitStatus.ACTION_NEEDED)
//                    stateController = ReadyToCommitState()
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

        private inner class StateController(initStatus: InsertUnitStatus) {
            private var state: UnitState = pickUpState(initStatus).also {
                status = initStatus
                it.onAttach()
            }


            suspend fun commit() = state.commit()
            fun excludeFromCommit() = state.excludeFromCommit()
            fun includeInCommit() = state.includeInCommit()


            fun changeState(newStatus: InsertUnitStatus) {
                state.onDetach()
                state = pickUpState(newStatus)
                status = newStatus
                state.onAttach()
            }

            private fun pickUpState(status: InsertUnitStatus): UnitState = when (status) {
                InsertUnitStatus.READY_TO_COMMIT -> ReadyToCommitState()
                InsertUnitStatus.ACTION_NEEDED -> ActionNeededState()
                InsertUnitStatus.EXCLUDED_FROM_COMMIT -> ExcludedFromCommitState()
                InsertUnitStatus.COMMITTING -> CommittingState()
                InsertUnitStatus.COMMITTED -> CommittedState()
            }


            private abstract inner class UnitState {
                open suspend fun commit() {
                    throw IllegalStateException("You can't commit unit with state: $this")
                }

                open fun excludeFromCommit() {
                    throw IllegalStateException("You can't exclude unit with state: $this")
                }

                open fun includeInCommit() {
                    throw IllegalStateException("You can't include unit with state: $this")
                }


                open fun onAttach() {}

                open fun onDetach() {}
            }

            private inner class ReadyToCommitState : UnitState() {
                override suspend fun commit() {
                    changeState(InsertUnitStatus.COMMITTING)

                    val (reset, inserted) = validWords
                            .map { database.resolveWord(it) }
                            .partition { it == WorderInsertDB.ResolveRes.RESET }

                    this@SimpleInsertModel.stats.apply {
                        this.reset += reset.size
                        this.inserted += inserted.size
                    }

                    changeState(InsertUnitStatus.COMMITTED)
                }

                override fun excludeFromCommit() = changeState(InsertUnitStatus.EXCLUDED_FROM_COMMIT)

                override fun onAttach() {
                    this@SimpleInsertModel.status = InsertModelStatus.READY_TO_COMMIT
                }
            }

            private inner class ActionNeededState : UnitState() {
                init {
                    stats.actionNeededUnits++
                }

                override fun excludeFromCommit() {
//                    stateController = ExcludedFromCommitState()
                }
            }

            private inner class ExcludedFromCommitState : UnitState() {
                override fun includeInCommit() {
//                    stateController = ReadyToCommitState()
                }
            }

            private inner class CommittingState : UnitState()

            private inner class CommittedState : UnitState() {
                init {
                    this@SimpleInsertModel.apply {
                        status = if (uncommittedUnits.isEmpty()) InsertModelStatus.COMMITTED else InsertModelStatus.PARTIALLY_COMMITTED
                        stats.committedUnits++
                        uncommittedUnits.remove(this@SimpleInsertUnit)
                    }
                }
            }
        }
    }
}
