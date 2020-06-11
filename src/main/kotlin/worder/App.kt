package worder

import javafx.beans.property.SimpleListProperty
import javafx.stage.Stage
import tornadofx.App
import tornadofx.find
import tornadofx.getValue
import tornadofx.observableListOf
import worder.controllers.DatabaseController
import worder.controllers.InserterController
import worder.model.SimpleObservableStats
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
        //testStats()
        // DEV
    }
}


// TODO Remove this out of this file

class TestClass {
    val listProperty1 = SimpleListProperty(observableListOf<String>())
    val list1: MutableList<String> by listProperty1

    val listProperty2 = SimpleListProperty(observableListOf("hell1", "hell2", "hell3"))
    val list2: MutableList<String> by listProperty2

    val stats = object : SimpleObservableStats("Test") {
        val size1: Int by bind(listProperty1.sizeProperty(), "size of list1")
        val size2: Int by bind(listProperty2.sizeProperty(), "size of list2")
    }

    var num1: Int by SimpleObservableStats.bind(stats, 15)
    var num2: Int by SimpleObservableStats.bind(stats, 100)
}

fun testStats() {
    val obj = TestClass()
    val stats = obj.stats

    println(stats.asMap)
    println(stats.asTitledMap)
    println()

    stats.updateTitle("size1", "2")
    stats.updateTitle("size2", "1")
    stats.updateTitle("num1", "16")
    stats.updateTitle("num2", "99")

    obj.apply {
        list1.add("Hell1")
        list1.add("Hell2")

        list2.removeAt(0)
        list2.removeAt(0)

        num1++
        num2--
    }

    stats.updateTitle("num2", "num2")

    println(stats.asMap)
    println(stats.asTitledMap)
    println()
}
