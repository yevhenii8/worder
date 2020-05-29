package worder.views

import tornadofx.View
import tornadofx.borderpane
import tornadofx.drawer
import tornadofx.label
import worder.model.insert.InsertBlock
import worder.views.fragments.InsertBlockFragment

class MainView : View("Worder GUI") {
    override val root = borderpane {
        top<DashboardView>()

        center = drawer {
            item<DatabaseView>()

            item("Updater") {
                label("updater view")
            }

            item<InserterView>(expanded = true)
        }

//        center = tabpane {
//            addClass(DrawerStyles.drawerItem)
//            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
//            side = LEFT
//
//            tab<DatabaseView>() {
//                addClass(DrawerStyles.drawerItem)
//            }
//
//            tab("Updater") {
//                addClass(DrawerStyles.drawerItem)
//                label("updater view")
//            }
//
//            tab("Inserter") {
//                addClass(DrawerStyles.drawerItem)
//                label("inserter view")
//            }
//        }

        bottom = label("network working / session timer / trademark / something else")
    }
}
