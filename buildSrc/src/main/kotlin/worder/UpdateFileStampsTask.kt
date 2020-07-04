package worder

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File
import javax.inject.Inject

open class UpdateFileStampsTask @Inject constructor(
        private val sourcesDir: File,
        private val sourcesFormats: List<String>
) : DefaultTask() {
    init {
        group = "Documentation"
        description = "Updates stamps at the beginning of source files."
    }


    @TaskAction
    fun execute() {
        val logger = logger

        val (valid, invalid) = sourcesDir.walk()
                .filter { file -> sourcesFormats.any { file.name.endsWith(it) } }
                .map { file -> file to StampedSourceFile.fromFile(file) }
                .partition { it.second != null }

        if (invalid.isNotEmpty()) {
            invalid.forEach {
                logger.error("Source file contains invalid Stamp: ${it.first}")
            }
            error("Invalid source file stamp(s) occurred! Source files' stamp updating has been cancelled!")
        }

        valid.forEach {
            it.second?.update()
        }
    }
}
