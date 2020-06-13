package worder

import javafx.stage.Stage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tornadofx.App
import tornadofx.find
import worder.controllers.DatabaseController
import worder.controllers.InserterController
import worder.model.SimpleObservableStats
import worder.views.MainView
import worder.views.styles.WorderStyle
import java.io.File
import kotlin.system.measureTimeMillis

class AppEntry : App(MainView::class, WorderStyle::class) {
    override fun start(stage: Stage) {
        stage.apply {
            icons += resources.image("/icons/worder-icon_512x512.png")
            isMaximized = true
        }

        super.start(stage)

        // DEV
//        find<DatabaseController>().connectToSqlLiteFile(File("/home/yevhenii/Projects/worder/sample-bd.bck"))
//        find<MainView>().switchToInserterTab()
//        find<InserterController>().uploadFiles(
//                listOf
//                (
//                        File("/home/yevhenii/Other/inserter/words0.txt"),
//                        File("/home/yevhenii/Other/inserter/words1.txt"),
//                        File("/home/yevhenii/Other/inserter/words2.txt"),
//                        File("/home/yevhenii/Other/inserter/words3.txt"),
//                        File("/home/yevhenii/Other/inserter/words4.txt")
//                )
//        )
        testStats()
        // DEV
    }
}

fun testStats() {
    val stats = object : SimpleObservableStats("Test stats") {
        var counter: Int by bindToStats(0)
    }

    val time = measureTimeMillis {
        runBlocking(Dispatchers.Default) {
            repeat(100000) {
                launch {
                    stats.counter++
                }
            }
        }
    }

    println("Time: $time")
    println("Counter: ${stats.counter}]")
}
