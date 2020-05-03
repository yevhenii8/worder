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

class MainView : View("Worder GUI") {
    val controller: MainController by inject()

    override val root = borderpane {
        top = label("center")

        center = drawer {

            item("Database", expanded = true) {
                label("database view")
                button("total++") {
                    action {
                    }
                }
                button("unlearned--") {
                    action {
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