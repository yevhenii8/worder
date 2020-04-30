package worder.views

import tornadofx.*
import worder.controllers.MainController

class MainView: View("Worder GUI") {
    val controller: MainController by inject()

    override val root = borderpane {
        top = label("Some database data, base using/db statistics, charts")

        center = drawer {

            item("Database", expanded = true) {
                label("database view")
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