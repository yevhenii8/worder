package worder

import javafx.stage.Stage
import tornadofx.App
import worder.controllers.DatabaseController
import worder.views.MainView
import worder.views.WorderStyle


class AppEntry : App(MainView::class, WorderStyle::class) {
    // TODO("Only for debugging")
    private val databaseController: DatabaseController by inject()

    override fun start(stage: Stage) {
        super.start(stage)
        stage.isMaximized = true
        databaseController.connectToSqlLiteFile("/home/yevhenii/Projects/worder/sample-bd.bck")
    }
}
