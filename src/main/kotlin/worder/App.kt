package worder

import javafx.stage.Stage
import tornadofx.App
import tornadofx.find
import worder.controllers.DatabaseController
import worder.controllers.InserterController
import worder.views.MainView
import worder.views.styles.WorderStyle
import java.io.File

class AppEntry : App(MainView::class, WorderStyle::class) {
    override fun start(stage: Stage) {
        stage.apply {
            icons += resources.image("/icons/worder-icon_512x512.png")
            isMaximized = true
        }

        super.start(stage)

        // DEV
        find<DatabaseController>().connectToSqlLiteFile(File("/home/yevhenii/Projects/worder/sample-bd.bck"))
        find<MainView>().switchToInserterTab()
        find<InserterController>().uploadFiles(
                listOf
                (
                        File("/home/yevhenii/Other/inserter/words0.txt"),
                        File("/home/yevhenii/Other/inserter/words1.txt"),
                        File("/home/yevhenii/Other/inserter/words2.txt"),
                        File("/home/yevhenii/Other/inserter/words3.txt"),
                        File("/home/yevhenii/Other/inserter/words4.txt")
                )
        )
        // DEV
    }
}
