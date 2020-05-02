package worder.views

import javafx.scene.control.Label
import tornadofx.View
import tornadofx.action
import tornadofx.borderpane
import tornadofx.button
import tornadofx.drawer
import tornadofx.gridpane
import tornadofx.label
import tornadofx.row
import tornadofx.text
import tornadofx.vbox
import worder.controllers.MainController
import worder.model.database.DatabaseSummary

class MainView : View("Worder GUI") {
    val controller: MainController by inject()
    private val testStat = DatabaseSummary("TestOBJ", 1000, 100, 900)

    init {
        testStat.subscribe {
            totalLabel.text = total.toString()
            unlearnedLabel.text = unlearned.toString()
            learnedLabel.text = learned.toString()
        }
    }

    private lateinit var totalLabel: Label
    private lateinit var unlearnedLabel: Label
    private lateinit var learnedLabel: Label

    override val root = borderpane {
        top =

        center = drawer {

            item("Database", expanded = true) {
                label("database view")
                button("total++") {
                    action {
                        testStat.total++
                    }
                }
                button("unlearned--") {
                    action {
                        testStat.unlearned--
                    }
                }
            }

            item("Updater") {
                label("updater view")
            }

            item("Inserter") {
                label("inserter view")
            }

        }

        bottom = label("network working / session timer / trademark / something else")
    }
}