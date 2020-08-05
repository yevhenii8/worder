package worder.buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import worder.buildsrc.StampedFile
import java.io.File

open class UpdateFileStampsTask : DefaultTask() {
    @InputDirectory
    var sourcesDir: File? = null

    @Input
    var sourcesFormats: List<String> = emptyList()

    @Option(description = "Run the task in the Transit mode. Use when updating a stamp structure.")
    var useTransit = false


    init {
        group = "Documentation"
        description = "Updates stamps at beginning of the files specified by extension and directory (recursively)."
    }


    @TaskAction
    fun execute() {
        if (sourcesDir == null)
            error("sourceDir input has not been set!")

        val (valid, invalid) = sourcesDir!!.walk()
                .filter { file -> sourcesFormats.any { file.name.endsWith(it) } }
                .map { file -> file to StampedFile(file, useTransit) }
                .partition { it.second.isStampValid }

        if (invalid.isNotEmpty()) {
            invalid.forEach {
                logger.error(it.second.validationResult)
            }
            error("Invalid source file stamp(s) occurred! Source files' stamps updating has been cancelled!")
        }

        valid.forEach {
            it.second.updateStamp()
        }
    }
}
