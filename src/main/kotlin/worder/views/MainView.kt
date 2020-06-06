package worder.views

import javafx.geometry.Pos
import tornadofx.Drawer
import tornadofx.DrawerItem
import tornadofx.View
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.drawer
import tornadofx.hbox
import tornadofx.hyperlink
import worder.views.styles.WorderStyle

class MainView : View("Worder GUI") {
    private val drawer: Drawer = drawer { }
    private val databaseTab: DrawerItem = drawer.item<DatabaseView>().apply { expanded = true }
    private val updaterTab: DrawerItem = drawer.item<UpdaterView>()
    private val inserterTab: DrawerItem = drawer.item<InserterView>()


    override val root = borderpane {
        top<DashboardView>()

        center = drawer

        bottom = hbox(alignment = Pos.CENTER) {
            addClass(WorderStyle.statusBar)

            hyperlink("Copyright yevhenii8 ® 2020") {
                setOnAction {
                    hostServices.showDocument("https://github.com/yevhenii8/worder")
                }
            }
        }
    }


    fun switchToDatabaseTab() {
        databaseTab.expanded = true
    }
}
