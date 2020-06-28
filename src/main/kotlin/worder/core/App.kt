package worder.core

import javafx.stage.Stage
import tornadofx.App
import tornadofx.FX
import tornadofx.find
import worder.core.styles.WorderDefaultStyles
import worder.core.view.MainView
import worder.database.DatabaseController
import worder.insert.InsertController
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

class AppEntry : App(MainView::class, WorderDefaultStyles::class) {
    companion object {
        private val databaseController: DatabaseController = find()
        private val samplePath: Path = Path.of("stuff").resolve("sample")
        private val originalSampleDB: File = samplePath.resolve("sample-db.bck").toFile()

        val sampleDB: File = samplePath.resolve("sample-db_TMP.bck").toFile()
        val isConnectedToSample: Boolean
            get() = databaseController.db?.toString() == sampleDB.name
        var keepSample: Boolean = false


        private fun withSampleDB(block: (samplePath: Path) -> Unit) {
            if (!isConnectedToSample) {
                require(originalSampleDB.exists()) {
                    "There's no sample-db provided! Please check: $originalSampleDB"
                }

                originalSampleDB.copyTo(sampleDB, overwrite = true)
                databaseController.connectToSqlLiteFile(sampleDB)
            }

            block.invoke(samplePath)
        }


        fun runDevSample() = withSampleDB { }

        fun runDevInsert() = withSampleDB { samplePath ->
            find<MainView>().switchToInsertTab()
            find<InsertController>().generateInsertModel(
                    listOf
                    (
                            File("$samplePath/inserting/words0.txt"),
                            File("$samplePath/inserting/words1.txt"),
                            File("$samplePath/inserting/words2.txt"),
                            File("$samplePath/inserting/words3.txt"),
                            File("$samplePath/inserting/words4.txt")
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

    override fun stop() {
        if (isConnectedToSample && !keepSample)
            sampleDB.delete()

        super.stop()
    }


    private fun processArguments() {
        val mappedArgs = FX.application.parameters.raw
                .associateWith { WorderArgument.fromString(it) }

        require(mappedArgs.all { it.value != null }) {
            "Unexpected argument(s) has been passed! ${mappedArgs.filterValues { it == null }.keys}"
        }

        mappedArgs.forEach { it.value!!.action.invoke() }
    }


    enum class WorderArgument(val value: String, val description: String, val action: () -> Unit) {
        DEV_DATABASE("--dev-sample", "Automatically connects to the sampleDB.", AppEntry.Companion::runDevSample),
        DEV_INSERT("--dev-insert", "Development stage for the Insert Tab.", AppEntry.Companion::runDevInsert),
        KEEP_SAMPLE_DB("--keep-sample-db", "Preserves sample-db-tmp file from removing at the end.", { withSampleDB { keepSample = true } });


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