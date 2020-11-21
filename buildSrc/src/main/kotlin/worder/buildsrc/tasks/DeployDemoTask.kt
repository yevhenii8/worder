package worder.buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import worder.commons.IOExchanger
import java.nio.file.Files

open class DeployDemoTask : DefaultTask() {
    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var deployExchanger: IOExchanger


    init {
        group = "Distribution"
        description = "Publishes Worder's demo-files with specified Exchanger."
    }


    @TaskAction
    fun execute() {
        require(this::deployExchanger.isInitialized) {
            "Please make sure you have specified deployer object!"
        }

        with(deployExchanger) {
            val logger = logger
            val demoDir = project.projectDir.toPath().resolve("demo")
            val remoteFiles = listAsStrings("demo")

            if (remoteFiles != null)
                listAsStrings("demo").forEach {
                    logger.info("Deleting 'demo/$it' ...")
                    deleteFile("demo/$it")
                }

            Files.walk(demoDir)
                    .forEach {
                        if (Files.isRegularFile(it) && !it.fileName.toString().endsWith("tmp.bck")) {
                            val pathToUpload = "demo/${demoDir.relativize(it)}"
                            logger.info("Uploading '$pathToUpload' ...")
                            uploadFile(pathToUpload, Files.readAllBytes(it), true)
                        }
                    }
        }
    }
}
