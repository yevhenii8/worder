package worder.views

import javafx.scene.Parent
import tornadofx.View
import tornadofx.label
import tornadofx.replaceChildren
import tornadofx.stackpane
import worder.controllers.DatabaseChangeListener
import worder.controllers.DatabaseController
import worder.views.fragments.NoConnectionFragment

class UpdaterView : View("Updater"), DatabaseChangeListener {
    private val noConnectionFragment = find<NoConnectionFragment>().root


    init {
        find<DatabaseController>().subscribe(this)
    }


    override val root: Parent = stackpane {
        add(noConnectionFragment)
    }


    override fun onDatabaseConnection() {
        root.replaceChildren(label("Connected!"))
    }

    override fun onDatabaseDisconnection() {
        root.replaceChildren(noConnectionFragment)
    }
}
