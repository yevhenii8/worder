package worder.core

import javafx.stage.Stage
import tornadofx.App
import tornadofx.FX
import tornadofx.find
import worder.core.view.MainView
import worder.core.view.WorderBrightStyles
import worder.database.DatabaseController
import worder.insert.InsertController
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class AppEntry : App(MainView::class, WorderBrightStyles::class) {
    companion object {
        private val databaseController: DatabaseController = find()
        private val appHomeDir: Path = Paths.get("").toAbsolutePath()
        private val sampleDir: Path = appHomeDir.resolve("sample")


        private fun withSampleDB(block: (sampleDir: Path) -> Unit) {
            require(appHomeDir.resolve("sample").toFile().exists()) {
                "There's no sample data provided! 'sample' directory doesn't exist!"
            }

            if (databaseController.db?.toString() != "md-2019-12-21--18-32-51.bck") {
                val originalSample = File("$sampleDir/md-2019-12-21--18-32-51.bck")
                val tmpSample = File("$sampleDir/md-2019-12-21--18-32-51_TMP.bck")
                originalSample.copyTo(tmpSample, overwrite = true)
                databaseController.connectToSqlLiteFile(tmpSample)
            }

            block.invoke(sampleDir)
        }


        fun runDevDatabase() = withSampleDB { }

        fun runDevInsert() = withSampleDB {
            find<MainView>().switchToInsertTab()
            find<InsertController>().generateInsertModel(
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


    override fun start(stage: Stage) {
        stage.apply {
            icons += resources.image("/icons/worder-icon_512x512.png")
            isMaximized = true
        }

        super.start(stage)
        processArguments()
    }


    private fun processArguments() {
        FX.application.parameters.raw
                .map { WorderArgument.fromString(it) }
                .requireNoNulls()
                .forEach { it.action.invoke() }
    }


    enum class WorderArgument(val value: String, val description: String, val action: () -> Unit) {
        DEV_DATABASE("--dev-database", "Development stage for the Database unit.", AppEntry.Companion::runDevDatabase),
        DEV_INSERT("--dev-insert", "Development stage for the Insert unit.", AppEntry.Companion::runDevInsert);


        companion object {
            fun fromString(value: String): WorderArgument? {
                for (worderArgument in values())
                    if (worderArgument.value == value)
                        return worderArgument

                return null
            }
        }
    }
}
