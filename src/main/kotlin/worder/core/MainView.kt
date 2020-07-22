/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <MainView.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <22/07/2020, 06:45:18 PM>
 * Version: <14>
 */

package worder.core

import javafx.geometry.Pos
import tornadofx.Drawer
import tornadofx.DrawerItem
import tornadofx.View
import tornadofx.addClass
import tornadofx.borderpane
import tornadofx.drawer
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.imageview
import worder.core.styles.WorderDefaultStyles
import worder.database.DatabaseTabView
import worder.insert.InsertTabView
import worder.update.UpdateTabView

class MainView : View("Worder GUI") {
    private val drawer: Drawer = drawer { }
    private val databaseTab: DrawerItem = drawer.item<DatabaseTabView>().apply { expanded = true }
    private val updaterTab: DrawerItem = drawer.item<UpdateTabView>()
    private val inserterTab: DrawerItem = drawer.item<InsertTabView>()


    override val root = borderpane {
        top<DatabaseDashboardView>()

        center = drawer

        bottom = hbox(alignment = Pos.CENTER) {
            addClass(WorderDefaultStyles.statusBar)

            hyperlink(text = "© 2019-2020 Yevhenii Nadtochii No Rights Reserved", graphic = imageview("/icons/github-icon_32x32.png")) {
                setOnAction {
                    hostServices.showDocument("https://github.com/yevhenii8/worder")
                }
            }
        }
    }


    fun switchToDatabaseTab() {
        databaseTab.expanded = true
    }

    fun switchToInsertTab() {
        inserterTab.expanded = true
    }

    fun switchToUpdateTab() {
        updaterTab.expanded = true
    }
}
