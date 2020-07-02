/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <generateFileStamps.sh>
 *
 * Created: <7/2/20, 11:27 PM>
 * Modified: <7/2/20, 11:50 PM>
 * Version: <1>
 */

package worder.database.view

import javafx.geometry.Pos
import javafx.scene.Parent
import tornadofx.Fragment
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.label
import tornadofx.vbox
import worder.core.view.MainView

class NoConnectionFragment : Fragment() {
    override val root: Parent = hbox(alignment = Pos.CENTER) {
        imageview("/icons/database-disconnected-icon_64x64.png")

        vbox(alignment = Pos.CENTER) {
            label("There's no database connection established!")

            hbox(alignment = Pos.CENTER) {
                label("Please,")
                hyperlink(" connect ") {
                    setOnAction {
                        find<MainView>().switchToDatabaseTab()
                    }
                }
                label("to a database firstly.")
            }
        }
    }
}
