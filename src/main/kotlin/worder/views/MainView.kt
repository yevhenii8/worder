package worder.views

import tornadofx.View
import tornadofx.borderpane
import tornadofx.drawer
import tornadofx.label

class MainView : View("Worder GUI") {
    override val root = borderpane {
        top<DashboardView>()

        center = drawer {

            item<DatabaseView>(expanded = true)

            item("Updater") { label("updater view") }

            item("Inserter") { label("inserter view") }

        }

        bottom = label("network working / session timer / trademark / something else")
    }
}