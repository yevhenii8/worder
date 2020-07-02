package worder

import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.collections.containNull
import io.kotest.matchers.collections.containOnlyNulls
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import java.nio.file.Path

class StampedSourceFileTest : ShouldSpec({
    val validStamp = """
            /**
             * Stamp was generated by <generateFileStamps.sh>
             * Last time was modified by <generateFileStamps.sh>
             *
             * Created: <7/2/20, 6:27 PM>
             * Modified: <7/2/20, 7:17 PM>
             * Version: <1>
             */

            package worder.core.model

            import javafx.scene.paint.Paint

            interface Status {
                val description: String
                val color: Paint
            }

        """.trimIndent()
    val invalidStampGeneral = """
            /**
             * My comment OOPs
             * Last time was checked by <generateFileStamps.sh> at <Sat Jun 27 23:02:07 2020>
             *
             * Created: <Sat Jun 27 22:51:50 2020>
             * Modified: <Wed Jun 24 17:27:24 2020>
             * Version: #<1>
             */

            package worder.core.model

            import javafx.scene.paint.Paint

            interface Status {
                val description: String
                val color: Paint
            }

        """.trimIndent()
    val invalidStampDate = """
            /**
             * Stamp was generated by <generateFileStamps.sh>
             * Last time was modified by <generateFileStamps.sh>
             *
             * Created: <7/2/2020, 6:27 PM>
             * Modified: <7/2/2020, 7:17 PM>
             * Version: <1>
             */

            package worder.core.model

            import javafx.scene.paint.Paint

            interface Status {
                val description: String
                val color: Paint
            }

        """.trimIndent()
    val noStamp = """
            package worder.core.model

            import javafx.scene.paint.Paint

            interface Status {
                val description: String
                val color: Paint
            }

        """.trimIndent()

    val testDir = Path.of("build", "tests", "StampedSourceFileTest").toFile()
    val validStampFiles = listOf(
            testDir.resolve("validStamp.kt"),
            testDir.resolve("validStamp.kts")
    )
    val invalidStampFiles = listOf(
            testDir.resolve("invalidStamp.kt"),
            testDir.resolve("invalidStamp.kts")
    )
    val noStampFiles = listOf(
            testDir.resolve("noStamp.kt"),
            testDir.resolve("noStamp.kts")
    )


    beforeSpec {
        if (!testDir.exists()) {
            testDir.mkdirs()

            validStampFiles.forEach {
                it.writeText(validStamp)
            }

            invalidStampFiles[0].writeText(invalidStampGeneral)
            invalidStampFiles[1].writeText(invalidStampDate)

            noStampFiles.forEach {
                it.writeText(noStamp)
            }
        }
    }


    xshould("work with valid stamp") {
        validStampFiles.map { StampedSourceFile.fromFile(it) } shouldNot containNull()
    }

    xshould("work with files with no stamp") {
        noStampFiles.map { StampedSourceFile.fromFile(it) } shouldNot containNull()
    }

    xshould("not work with invalid stamp") {
        invalidStampFiles.map { StampedSourceFile.fromFile(it) } should containOnlyNulls()
    }

    xshould("process all files properly") {
        testDir.walk()
                .filter { it.name.endsWith(".kt") || it.name.endsWith(".kts") }
                .forEach { StampedSourceFile.fromFile(it)?.update() }
    }
})
