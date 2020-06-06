package worder.views

import javafx.scene.Parent
import javafx.stage.FileChooser.ExtensionFilter
import tornadofx.View
import tornadofx.replaceChildren
import tornadofx.stackpane
import worder.controllers.DatabaseController
import worder.controllers.DatabaseListener
import worder.controllers.InserterController
import worder.views.fragments.DragAndDropFragment
import worder.views.fragments.NoConnectionFragment
import java.io.File

class InserterView : View("Inserter"), DatabaseListener {
    private val noConnectionFragment = find<NoConnectionFragment>().root


    init {
        find<DatabaseController>().subscribe(this)
    }


    override var root: Parent = stackpane {
        add(noConnectionFragment)
    }


    override fun onDatabaseConnection() {
        val fragment = find<DragAndDropFragment>(
                "text" to "Drag one or more plain files here to process them",
                "windowTitle" to "Inserter Files Selection",
                "extensionFilter" to ExtensionFilter("Text Files (*.txt)", "*.txt"),
                "action" to { files: List<File> -> find<InserterController>().processFiles(files) }
        )

        root.replaceChildren(fragment.root)
    }

    override fun onDatabaseDisconnection() {
        root.replaceChildren(noConnectionFragment)
    }
}
