package worder.views

import tornadofx.View
import tornadofx.button
import tornadofx.hbox
import tornadofx.vbox
import worder.controllers.DatabaseController

class DatabaseView : View("Database") {
    private val controller: DatabaseController by inject()

    override val root = vbox {
        hbox {
            add(find<StatsBlockFragment>("stats" to controller.stats))
        }
        button("change stats")
    }
}
