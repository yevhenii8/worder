package worder.views

import javafx.geometry.Pos
import tornadofx.View
import tornadofx.hbox
import worder.controllers.DatabaseController

class DashboardView : View() {
    private val databaseController: DatabaseController by inject()

    override val root = hbox(alignment = Pos.CENTER) {
        add(find<StatsBlockFragment>("stats" to databaseController.stats))
    }
}
