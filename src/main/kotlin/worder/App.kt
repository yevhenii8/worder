package worder

import javafx.stage.Stage
import tornadofx.App
import tornadofx.FX
import tornadofx.find
import worder.controllers.DatabaseController
import worder.controllers.InsertController
import worder.views.MainView
import worder.views.styles.WorderStyle
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class AppEntry : App(MainView::class, WorderStyle::class) {
    private val appHomeDir: Path = Paths.get("").toAbsolutePath()
    private val sampleDir: Path = appHomeDir.resolve("sample")


    override fun start(stage: Stage) {
        stage.apply {
            icons += resources.image("/icons/worder-icon_512x512.png")
            isMaximized = true
        }

        super.start(stage)
        processArguments()
    }


    private fun processArguments() {
        fun processArgument(argument: String) = when (argument) {
            "--withSample" -> connectToSampleDB()
            "--devInsert" -> devInsert()
            else -> throw IllegalArgumentException("Unknown argument has been passed: $argument")
        }

        FX.application.parameters.raw.forEach(::processArgument)
    }

    private fun withSample(block: (sampleDir: Path) -> Unit) {
        if (!appHomeDir.resolve("sample").toFile().exists())
            throw IllegalStateException("There's no sample data provided! 'sample' directory doesn't exist!")

        block.invoke(sampleDir)
    }

    private fun connectToSampleDB() = withSample {
        find<DatabaseController>().connectToSqlLiteFile(File("$it/md-2019-12-21--18-32-51.bck"))
    }

    private fun devInsert() = withSample {
        connectToSampleDB()

        find<MainView>().switchToInsertTab()
        find<InsertController>().uploadFiles(
                listOf
                (
                        File("$it/inserting/words0.txt"),
                        File("$it/inserting/words1.txt"),
                        File("$it/inserting/words2.txt"),
                        File("$it/inserting/words3.txt"),
                        File("$it/inserting/words4.txt")
                )
        )
    }
}
