package worder

import javafx.stage.Stage
import tornadofx.App
import tornadofx.find
import worder.controllers.DatabaseController
import worder.views.MainView
import worder.views.styles.WorderStyle


class AppEntry : App(MainView::class, WorderStyle::class) {
    override fun start(stage: Stage) {
        stage.apply {
            icons += resources.image("/book.png")
            isMaximized = true
        }
        super.start(stage)

        find<DatabaseController>().connectToSqlLiteFile("/home/yevhenii/Projects/worder/sample-bd.bck")
    }
}
