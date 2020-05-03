package worder.views

import javafx.scene.Parent
import tornadofx.View
import worder.controllers.DatabaseController

class DatabaseView : View() {
    val controller: DatabaseController by inject()

    override val root: Parent
        get() = TODO("Not yet implemented")
}
