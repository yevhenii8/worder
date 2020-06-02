package worder.views

import javafx.geometry.Pos
import javafx.scene.layout.Background
import tornadofx.View
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.drawer
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.label
import tornadofx.onRightClick
import tornadofx.text
import tornadofx.vbox
import worder.views.styles.WorderStyle

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

        bottom = hbox(alignment = Pos.CENTER) {
            addClass(WorderStyle.statusBar)

            hyperlink("Copyright yevhenii8 ® 2020") {
                setOnAction {
                    hostServices.showDocument("https://github.com/yevhenii8/worder")
                }
            }
        }
    }
}
