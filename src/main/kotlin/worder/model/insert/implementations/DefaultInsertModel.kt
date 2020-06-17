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
import worder.model.applySynchronized
import worder.model.database.WorderInsertDB
import worder.model.insert.InsertModel
import worder.model.insert.InsertModel.InsertModelStatus
import worder.model.insert.InsertUnit
import worder.model.insert.InsertUnit.InsertUnitStatus
import worder.model.insert.InsertUnit.InvalidWord
import java.io.File

class DefaultInsertModel private constructor(private val database: WorderInsertDB, files: List<File>) : InsertModel {
    companion object {
        fun createInstance(database: WorderInsertDB, files: List<File>): InsertModel = DefaultInsertModel(database, files)
    }


    override val modelStatusProperty: ObjectProperty<InsertModelStatus> = SimpleObjectProperty(InsertModelStatus.CREATED)
    override var modelStatus: InsertModelStatus by modelStatusProperty

    override val uncommittedUnitsProperty: ListProperty<InsertUnit> = SimpleListProperty(observableListOf())
    override val uncommittedUnits: MutableList<InsertUnit> by uncommittedUnitsProperty

    override val committedUnitsProperty: ListProperty<InsertUnit> = SimpleListProperty(observableListOf())
    override val committedUnits: MutableList<InsertUnit> by committedUnitsProperty

    override val stats: SimpleInsertModelStats = object : SimpleInsertModelStats() {
        override var uncommittedUnits: Int by bindToStats(uncommittedUnitsProperty.sizeProperty())
        override var committedUnits: Int by bindToStats(committedUnitsProperty.sizeProperty())
    }


    init {
        files.forEach {
            if (!(it.isFile && it.canRead()))
                throw IllegalArgumentException("Please provide correct readable file!")
        }

        files.forEachIndexed { index, file ->
            val (validWords, invalidWords) = file.readLines()
                    .distinct()
                    .map { it.trim() }
                    .partition { BareWord.wordValidator.invoke(it) }

            val newUnit = SimpleInsertUnit(
                    id = "Unit_$index",
                    source = file.name,
                    validWords = validWords,
                    invalidWords = invalidWords
            )

            uncommittedUnits.add(newUnit)

            stats.apply {
                totalValidWords += validWords.size
                totalInvalidWords += invalidWords.size
                generatedUnits++
            }
        }
    }


    override suspend fun commitAllUnits() {
        supervisorScope {
            uncommittedUnits
                    .filter { it.unitStatus == InsertUnitStatus.READY_TO_COMMIT }
                    .forEach { launch { it.commit() } }
        }
    }


    private fun updateModelStatus() {
        when {
            uncommittedUnits.any { it.unitStatus == InsertUnitStatus.READY_TO_COMMIT } -> {
                modelStatus = InsertModelStatus.READY_TO_COMMIT
            }

            uncommittedUnits.isEmpty() -> {
                modelStatus = InsertModelStatus.COMMITTED
            }

            committedUnits.isNotEmpty() && uncommittedUnits.isNotEmpty() -> {
                modelStatus = InsertModelStatus.PARTIALLY_COMMITTED
            }
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

        override val unitStatusProperty: ObjectProperty<InsertUnitStatus> = SimpleObjectProperty()
        override var unitStatus: InsertUnitStatus by unitStatusProperty

        override val sourceProperty: ReadOnlyStringProperty = SimpleStringProperty(source)
        override val source: String by sourceProperty

        override val validWordsProperty: ListProperty<BareWord> = SimpleListProperty()
        override val validWords: MutableList<BareWord> by validWordsProperty

        override val invalidWordsProperty: ListProperty<InvalidWord> = SimpleListProperty()
        override val invalidWords: MutableList<InvalidWord> by invalidWordsProperty


        init {
            validWordsProperty.set(validWords.map { BareWord(it) }.toObservable())
            invalidWordsProperty.set(invalidWords.map { SimpleInvalidWord(it) }.toObservable())
            stateController = StateController(InsertUnitStatus.READY_TO_COMMIT)
        }


        override suspend fun commit() = stateController.commit()
        override fun excludeFromCommit() = stateController.excludeFromCommit()
        override fun includeInCommit() = stateController.includeInCommit()


        private inner class SimpleInvalidWord(override val value: String) : InvalidWord {
            override fun reject() {
                invalidWords.remove(this)
                stateController.changeState(InsertUnitStatus.READY_TO_COMMIT)
            }

            override fun substitute(substitution: String): Boolean {
                if (BareWord.wordValidator.invoke(substitution))
                    return false

                validWords.add(BareWord(substitution))
                invalidWords.remove(this)
                stateController.changeState(InsertUnitStatus.READY_TO_COMMIT)

                return true
            }
        }

        private inner class StateController(initUnitStatus: InsertUnitStatus) {
            private var unitState: UnitState = pickUpState(initUnitStatus)


            init {
                unitStatus = initUnitStatus
                unitState.onAttach()
            }


            suspend fun commit() = unitState.commit()
            fun excludeFromCommit() = unitState.excludeFromCommit()
            fun includeInCommit() = unitState.includeInCommit()


            fun changeState(newUnitStatus: InsertUnitStatus) {
                unitState.onDetach()
                unitState = pickUpState(newUnitStatus)
                unitStatus = newUnitStatus
                unitState.onAttach()
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
                    throw IllegalStateException("You can't commit unit with status: ${this@SimpleInsertUnit.unitStatus}")
                }

                open fun excludeFromCommit() {
                    throw IllegalStateException("You can't exclude unit with status: ${this@SimpleInsertUnit.unitStatus}")
                }

                open fun includeInCommit() {
                    throw IllegalStateException("You can't include unit with status: ${this@SimpleInsertUnit.unitStatus}")
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

                    this@DefaultInsertModel.stats.applySynchronized {
                        this.reset += reset.size
                        this.inserted += inserted.size
                    }

                    changeState(InsertUnitStatus.COMMITTED)
                }

                override fun excludeFromCommit() {
                    changeState(InsertUnitStatus.EXCLUDED_FROM_COMMIT)
                }

                override fun onAttach() {
                    if (invalidWords.isEmpty())
                        updateModelStatus()
                    else
                        changeState(InsertUnitStatus.ACTION_NEEDED)
                }
            }

            private inner class ActionNeededState : UnitState() {
                override fun excludeFromCommit() {
                    changeState(InsertUnitStatus.EXCLUDED_FROM_COMMIT)
                }

                override fun onAttach() {
                    stats.actionNeededUnits++
                }

                override fun onDetach() {
                    stats.actionNeededUnits--
                }
            }

            private inner class ExcludedFromCommitState : UnitState() {
                override fun includeInCommit() {
                    changeState(InsertUnitStatus.READY_TO_COMMIT)
                }

                override fun onAttach() {
                    stats.excludedUnits++
                }

                override fun onDetach() {
                    stats.excludedUnits--
                }
            }

            private inner class CommittingState : UnitState()

            private inner class CommittedState : UnitState() {
                override fun onAttach() {
                    uncommittedUnits.remove(this@SimpleInsertUnit)
                    committedUnits.add(this@SimpleInsertUnit)
                }
            }
        }
    }
}
