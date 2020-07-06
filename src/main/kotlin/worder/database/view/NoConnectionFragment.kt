/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <NoConnectionFragment.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <06/07/2020, 07:25:08 PM>
 * Version: <4>
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
        imageview("/icons/database-disconnected_64x64.png")

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
