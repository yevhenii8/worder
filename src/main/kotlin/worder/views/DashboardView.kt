package worder.views

import javafx.scene.Parent
import tornadofx.Fragment
import tornadofx.View
import tornadofx.gridpane
import tornadofx.label
import tornadofx.row
import tornadofx.text
import tornadofx.vbox
import worder.controllers.DashboardController

class DashboardView : View() {
    val controller: DashboardController by inject()

    override val root = vbox {
        gridpane {
            row { label("Database Summary") }
            row {
                label("Total: ")
                //totalLabel = label(testStat.total.toString())
            }
            row {
                label("Unlearned: ")
                //unlearnedLabel = label(testStat.unlearned.toString())
            }
            row {
                label("Learned: ")
                //learnedLabel = label(testStat.learned.toString())
            }
        }
    }
}
