/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <AppEntry.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <12/11/2020, 10:32:15 PM>
 * Version: <140>
 */

package worder.gui

import javafx.stage.Stage
import tornadofx.App
import tornadofx.FX
import tornadofx.find
import worder.gui.core.MainView
import worder.gui.core.styles.WorderCustomStyles
import worder.gui.core.styles.WorderDefaultStyles
import worder.gui.database.DatabaseController
import worder.gui.insert.InsertTabView
import java.io.File
import java.nio.file.Path
import kotlin.system.exitProcess

class AppEntry : App(MainView::class, WorderDefaultStyles::class, WorderCustomStyles::class) {
    companion object {
        private val databaseController: DatabaseController = find()
        private val samplePath: Path = Path.of("sample")
        private val originalSampleDB: File = samplePath.resolve("sample-db.bck").toFile()
        private val devInsertFiles: List<File> = listOf(
                File("$samplePath/inserting/words0.txt"),
                File("$samplePath/inserting/words1.txt"),
                File("$samplePath/inserting/words2.txt"),
                File("$samplePath/inserting/words3.txt"),
                File("$samplePath/inserting/words4.txt"),
                File("$samplePath/inserting/words5.txt")
        )

        val worderVersion: String = Companion::class.java.getResource("/version").readText()
        val mainView: MainView = find()
        val sampleDB: File = samplePath.resolve("sample-db_TMP.bck").toFile()
        val isConnectedToSample: Boolean
            get() = databaseController.db?.toString()?.substringAfterLast('/') == sampleDB.name
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


        fun runDevInsert() = withSampleDB {
            mainView.switchToInsertTab()
            find<InsertTabView>().generateInsertBatch(devInsertFiles)
        }

        fun runDevInsertHard() = withSampleDB {
            val devInsertFilesMany = mutableListOf<File>().apply {
                repeat(10) {
                    addAll(devInsertFiles)
                }
            }

            mainView.switchToInsertTab()
            find<InsertTabView>().generateInsertBatch(devInsertFilesMany)
        }

        fun runDevUpdate() = withSampleDB {
            mainView.switchToUpdateTab()
        }

        fun runDevDebug() {
            println("Used JRE: ${Runtime.version()}")
            println("Used KotlinC: ${KotlinVersion.CURRENT}")

            println("worder classloader: ${this::class.java.classLoader}")
            println("worder classloader name: ${this::class.java.classLoader.name}")
            println("worder classloader parent: ${this::class.java.classLoader.parent}")
            println("worder classloader parent name: ${this::class.java.classLoader.parent.name}")
        }

        fun printHelpAndExit() {
            val arguments = WorderArgument.values()
            val maxLen = arguments.map { it.value.length }.max()!!

            arguments.forEach {
                println("${it.value}   " + " ".repeat(maxLen - it.value.length) + it.description)
            }

            exitProcess(0)
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
        databaseController.disconnect()

        if (isConnectedToSample && !keepSample)
            sampleDB.delete()

        super.stop()
    }


    private fun processArguments() {
        val mappedArgs = FX.application.parameters.raw
                .associateWith { str -> WorderArgument.values().find { it.value == str } }

        require(mappedArgs.all { it.value != null }) {
            "Unexpected argument(s): ${mappedArgs.filterValues { it == null }.keys}"
        }

        mappedArgs.apply {
            if (size > 0)
                mainView.title += " (${keys.joinToString(" ")})"
            forEach { it.value!!.action.invoke() }
        }
    }


    enum class WorderArgument(val value: String, val description: String, val action: () -> Unit) {
        HELP("--help", "Prints all possible worder arguments and exits.", AppEntry.Companion::printHelpAndExit),
        DEV_DEBUG("--dev-debug", "Prints additional info (JRE, KotlinC) when app starts.", AppEntry.Companion::runDevDebug),
        DEV_DATABASE("--dev-sample", "Automatically connects to the sampleDB.", { withSampleDB { } }),
        DEV_DATABASE_KEEP("--dev-sample-keep", "Automatically connects to the sampleDB and does NOT remove it on exit.", { withSampleDB { keepSample = true } }),
        DEV_INSERT("--dev-insert", "Development stage for the Insert Tab.", AppEntry.Companion::runDevInsert),
        DEV_INSERT_HARD("--dev-insert-hard", "Development stage for the Insert Tab with hard load.", AppEntry.Companion::runDevInsertHard),
        DEV_UPDATE("--dev-update", "Development stage for the Update Tab.", AppEntry.Companion::runDevUpdate);
    }
}
