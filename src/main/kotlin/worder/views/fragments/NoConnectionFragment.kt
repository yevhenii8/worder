package worder.views.fragments

import javafx.geometry.Pos
import javafx.scene.Parent
import tornadofx.Fragment
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.label
import tornadofx.vbox
import worder.views.MainView

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
