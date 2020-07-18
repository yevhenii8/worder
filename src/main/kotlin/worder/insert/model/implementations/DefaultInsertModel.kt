/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DefaultInsertModel.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <18/07/2020, 10:44:32 PM>
 * Version: <17>
 */

package worder.insert.model.implementations

import javafx.beans.property.ListProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SetProperty
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleSetProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import tornadofx.getValue
import tornadofx.observableListOf
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


    // ALL THE UNITS, SPLIT BY THEIR CURRENT STATUS

    override val readyToCommitUnitsProperty: ListProperty<InsertUnit> = SimpleListProperty(observableListOf())
    override val readyToCommitUnits: MutableList<InsertUnit> by readyToCommitUnitsProperty

    override val actionNeededUnitsProperty: ListProperty<InsertUnit> = SimpleListProperty(observableListOf())
    override val actionNeededUnits: MutableList<InsertUnit> by actionNeededUnitsProperty

    override val excludedUnitsProperty: ListProperty<InsertUnit> = SimpleListProperty(observableListOf())
    override val excludedUnits: MutableList<InsertUnit> by excludedUnitsProperty

    override val committingUnitsProperty: ListProperty<InsertUnit> = SimpleListProperty(observableListOf())
    override val committingUnits: MutableList<InsertUnit> by committingUnitsProperty

    override val committedUnitsProperty: ListProperty<InsertUnit> = SimpleListProperty(observableListOf())
    override val committedUnits: MutableList<InsertUnit> by committedUnitsProperty


    // OTHER MODEL PROPERTIES

    override val modelStatusProperty: ObjectProperty<InsertModelStatus> = SimpleObjectProperty(InsertModelStatus.ACTION_NEEDED)
    override var modelStatus: InsertModelStatus by modelStatusProperty

    override val observableStats: SimpleInsertModelStats = SimpleInsertModelStats().apply {
        readyToCommitUnitsProperty.bind(this@DefaultInsertModel.readyToCommitUnitsProperty.sizeProperty())
        actionNeededUnitsProperty.bind(this@DefaultInsertModel.actionNeededUnitsProperty.sizeProperty())
        excludedUnitsProperty.bind(this@DefaultInsertModel.excludedUnitsProperty.sizeProperty())
        committedUnitsProperty.bind(this@DefaultInsertModel.committedUnitsProperty.sizeProperty())
    }


    init {
        require(files.isNotEmpty()) {
            "InsertModel can't be initialized without a file!"
        }

        files.forEach {
            require(it.isFile && it.canRead()) {
                "Please provide correct readable file! passed file: ${it.name}"
            }
        }

        files.forEachIndexed { index, file ->
            val (validWords, invalidWords) = file.readLines()
                    .distinct()
                    .map { it.trim() }
                    .partition { BareWord.wordValidator.invoke(it) }

            SimpleInsertUnit(
                    id = "Unit_$index",
                    source = file.name,
                    validWords = validWords,
                    invalidWords = invalidWords
            )

            observableStats.apply {
                totalValidWords += validWords.size
                totalInvalidWords += invalidWords.size
                generatedUnits++
            }
        }

        observableStats.totalWords = observableStats.totalInvalidWords + observableStats.totalValidWords
    }


    override suspend fun commitAllUnits() {
        supervisorScope {
            val toCommitUnits = ArrayList(readyToCommitUnits)
            toCommitUnits
                    .forEach { launch { it.commit() } }
        }
    }


    private fun updateModelStatus() {
        modelStatus = when {
            committingUnits.isNotEmpty() -> InsertModelStatus.COMMITTING
            readyToCommitUnits.isNotEmpty() -> InsertModelStatus.READY_TO_COMMIT
            actionNeededUnits.isNotEmpty() -> InsertModelStatus.ACTION_NEEDED
            committedUnits.size == observableStats.generatedUnits -> InsertModelStatus.COMMITTED
            excludedUnits.size == observableStats.generatedUnits -> InsertModelStatus.FULL_EXCLUDE
            else -> InsertModelStatus.PARTIALLY_COMMITTED
        }
    }


    private inner class SimpleInsertUnit(
            override val id: String,
            override val source: String,
            validWords: List<String>,
            invalidWords: List<String>
    ) : InsertUnit {
        private var stateController: StateController

        override val statusProperty: ObjectProperty<InsertUnitStatus> = SimpleObjectProperty()
        var unitStatus: InsertUnitStatus by statusProperty

        override val validWordsProperty: SetProperty<BareWord> = SimpleSetProperty()
        val validWords: MutableSet<BareWord> by validWordsProperty

        override val invalidWordsProperty: SetProperty<InvalidWord> = SimpleSetProperty()
        val invalidWords: MutableSet<InvalidWord> by invalidWordsProperty


        init {
            validWordsProperty.set(validWords.map { BareWord(it) }.toMutableSet().toObservable())
            invalidWordsProperty.set(invalidWords.map { SimpleInvalidWord(it) }.toMutableSet().toObservable())
            stateController = StateController(InsertUnitStatus.READY_TO_COMMIT)
        }


        override suspend fun commit() = stateController.commit()
        override fun exclude() = stateController.excludeFromCommit()
        override fun include() = stateController.includeInCommit()


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
                updateModelStatus()
            }


            suspend fun commit() = unitState.commit()
            fun excludeFromCommit() = unitState.excludeFromCommit()
            fun includeInCommit() = unitState.includeInCommit()


            fun changeState(newUnitStatus: InsertUnitStatus) {
                unitState.onDetach()
                unitState = pickUpState(newUnitStatus)
                unitStatus = newUnitStatus
                unitState.onAttach()
                updateModelStatus()
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
                    if (invalidWords.isNotEmpty())
                        changeState(InsertUnitStatus.ACTION_NEEDED)
                    else
                        readyToCommitUnits.add(this@SimpleInsertUnit)
                }

                override fun onDetach() {
                    readyToCommitUnits.remove(this@SimpleInsertUnit)
                }
            }

            private inner class ActionNeededState : UnitState() {
                override fun excludeFromCommit() {
                    changeState(InsertUnitStatus.EXCLUDED_FROM_COMMIT)
                }

                override fun onAttach() {
                    actionNeededUnits.add(this@SimpleInsertUnit)
                }

                override fun onDetach() {
                    actionNeededUnits.remove(this@SimpleInsertUnit)
                }
            }

            private inner class ExcludedFromCommitState : UnitState() {
                override fun includeInCommit() {
                    changeState(InsertUnitStatus.READY_TO_COMMIT)
                }

                override fun onAttach() {
                    excludedUnits.add(this@SimpleInsertUnit)
                }

                override fun onDetach() {
                    excludedUnits.remove(this@SimpleInsertUnit)
                }
            }

            private inner class CommittingState : UnitState() {
                override fun onAttach() {
                    committingUnits.add(this@SimpleInsertUnit)
                }

                override fun onDetach() {
                    committingUnits.remove(this@SimpleInsertUnit)
                }
            }

            private inner class CommittedState : UnitState() {
                override fun onAttach() {
                    committedUnits.add(this@SimpleInsertUnit)
                }
            }
        }
    }
}
