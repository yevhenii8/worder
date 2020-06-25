package worder.insert.model.implementations

import javafx.beans.property.ObjectProperty
import javafx.beans.property.ReadOnlyStringProperty
import javafx.beans.property.SetProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleSetProperty
import javafx.beans.property.SimpleStringProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import tornadofx.getValue
import tornadofx.observableSetOf
import tornadofx.setValue
import tornadofx.toObservable
import worder.core.model.BareWord
import worder.database.model.WorderInsertDB
import worder.insert.model.InsertModel
import worder.insert.model.InsertModel.InsertModelStatus
import worder.insert.model.InsertUnit
import worder.insert.model.InsertUnit.InsertUnitStatus
import worder.insert.model.InsertUnit.InvalidWord
import java.io.File

class DefaultInsertModel private constructor(private val database: WorderInsertDB, files: List<File>) : InsertModel {
    companion object {
        fun createInstance(database: WorderInsertDB, files: List<File>): InsertModel = DefaultInsertModel(database, files)
    }


    override val modelStatusProperty: ObjectProperty<InsertModelStatus> = SimpleObjectProperty(InsertModelStatus.CREATED)
    override var modelStatus: InsertModelStatus by modelStatusProperty

    override val uncommittedUnitsProperty: SetProperty<InsertUnit> = SimpleSetProperty(observableSetOf())
    override val uncommittedUnits: MutableSet<InsertUnit> by uncommittedUnitsProperty

    override val committedUnitsProperty: SetProperty<InsertUnit> = SimpleSetProperty(observableSetOf())
    override val committedUnits: MutableSet<InsertUnit> by committedUnitsProperty

    override val observableStats: SimpleInsertModelStats = object : SimpleInsertModelStats() {
        override var uncommittedUnits: Int by bindToStats(
                source = this@DefaultInsertModel.uncommittedUnitsProperty.sizeProperty(),
                usePropertyNameAsTitle = false
        )

        override var committedUnits: Int by bindToStats(
                source = this@DefaultInsertModel.committedUnitsProperty.sizeProperty(),
                usePropertyNameAsTitle = false
        )
    }


    init {
        files.forEach {
            require(it.isFile && it.canRead()) {
                "Please provide correct readable file! passed: ${it.name}"
            }
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

            observableStats.apply {
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

        override val validWordsProperty: SetProperty<BareWord> = SimpleSetProperty()
        override val validWords: MutableSet<BareWord> by validWordsProperty

        override val invalidWordsProperty: SetProperty<InvalidWord> = SimpleSetProperty()
        override val invalidWords: MutableSet<InvalidWord> by invalidWordsProperty


        init {
            validWordsProperty.set(validWords.map { BareWord(it) }.toMutableSet().toObservable())
            invalidWordsProperty.set(invalidWords.map { SimpleInvalidWord(it) }.toMutableSet().toObservable())
            stateController = StateController(InsertUnitStatus.READY_TO_COMMIT)
        }


        override suspend fun commit() = stateController.commit()
        override fun excludeFromCommit() = stateController.excludeFromCommit()
        override fun includeInCommit() = stateController.includeInCommit()


        private inner class SimpleInvalidWord(override val value: String) : InvalidWord {
            override fun reject() {
                invalidWords.remove(this)
                observableStats.totalInvalidWords--
                stateController.changeState(InsertUnitStatus.READY_TO_COMMIT)
            }

            override fun substitute(substitution: String): Boolean {
                if (!BareWord.wordValidator.invoke(substitution))
                    return false

                validWords.add(BareWord(substitution))
                invalidWords.remove(this)
                stateController.changeState(InsertUnitStatus.READY_TO_COMMIT)

                observableStats.apply {
                    totalInvalidWords--
                    totalValidWords++
                }

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
                    error("You can't commit unit with status: ${this@SimpleInsertUnit.unitStatus}")
                }

                open fun excludeFromCommit() {
                    error("You can't exclude unit with status: ${this@SimpleInsertUnit.unitStatus}")
                }

                open fun includeInCommit() {
                    error("You can't include unit with status: ${this@SimpleInsertUnit.unitStatus}")
                }


                open fun onAttach() {}

                open fun onDetach() {}
            }

            private inner class ReadyToCommitState : UnitState() {
                override suspend fun commit() {
                    withContext(Dispatchers.Main) {
                        changeState(InsertUnitStatus.COMMITTING)
                    }

                    val (reset, inserted) = database.resolveWords(validWords)
                            .entries
                            .partition { it.value == WorderInsertDB.ResolveRes.RESET }

                    withContext(Dispatchers.Main) {
                        observableStats.apply {
                            this.reset += reset.size
                            this.inserted += inserted.size
                            this.totalProcessed = this.reset + this.inserted
                        }

                        changeState(InsertUnitStatus.COMMITTED)
                    }
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
                    observableStats.actionNeededUnits++
                }

                override fun onDetach() {
                    observableStats.actionNeededUnits--
                }
            }

            private inner class ExcludedFromCommitState : UnitState() {
                override fun includeInCommit() {
                    changeState(InsertUnitStatus.READY_TO_COMMIT)
                }

                override fun onAttach() {
                    observableStats.excludedUnits++
                }

                override fun onDetach() {
                    observableStats.excludedUnits--
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
