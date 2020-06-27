package worder

import io.kotest.core.spec.style.ShouldSpec
import java.io.File

@Suppress("BlockingMethodInNonBlockingContext")
class GradleUpdateFileStampsTest : ShouldSpec({
    val testDir = File("build").resolve("tests").resolve("GradleUpdateFileStampsTest")
    val newFileFill = """
            package worder.core.model

            import javafx.scene.paint.Paint

            interface Status {
                val description: String
                val color: Paint
            }
        """.trimIndent() + "\n\r"
    val oldFileFill = """
            /**
             * Generated by Gradle <updateFileStamps> task
             * File created: 2020.06.27
             * Last reviewed: 2020.06.27
             */
 
            package worder.core.model

            import javafx.scene.paint.Paint

            interface Status {
                val description: String
                val color: Paint
            }
        """.trimIndent() + "\n\r"

    beforeSpec {
        if (testDir.exists())
            testDir.deleteRecursively()
        testDir.mkdirs()

        File("build.gradle.kts")
                .copyTo(testDir.resolve("build.gradle.kts"))

        testDir.apply {
            resolve("newFile.kt").writeText(newFileFill)
            resolve("oldFile.kt").writeText(oldFileFill)
            resolve("newFile.kts").writeText(newFileFill)
            resolve("oldFile.kts").writeText(oldFileFill)
            resolve("newFile.txt").writeText(newFileFill)
            resolve("oldFile.other").writeText(oldFileFill)
        }

        updateFileStamps(testDir)
    }


    should("prepend a stamp to a NEW file") {

    }

    should("update MODIFIED file's stamp") {

    }

    should("process only .kt and .kts files") {

    }
}) {
    companion object {
        fun updateFileStamps(projectDir: File) {
            val (oldFiles, newFiles) = projectDir.walkTopDown()
                    .filter { it.name.endsWith(".kt") || it.name.endsWith(".kts") }
                    .partition { it.readText().contains("Generated by Gradle <updateFileStamps> task") }

            if (newFiles.isNotEmpty())
                error("Some source files don't contain a file stamp! Please consider running stuff/bin/updateFilesStamp.sh")

            oldFiles.forEach { file ->

            }
        }
    }
}
