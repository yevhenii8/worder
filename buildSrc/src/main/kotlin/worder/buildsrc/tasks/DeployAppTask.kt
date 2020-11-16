package worder.buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.jvm.tasks.Jar
import worder.commons.AppDescriptor
import worder.commons.IOExchanger
import worder.commons.OS
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path

@Suppress("LeakingThis")
open class DeployAppTask : DefaultTask() {
    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var deployExchanger: IOExchanger

    @Option(description = "Use this option when AppDescriptor structure changes.")
    var removeAllDescriptors = false


    private val gradleLogger: Logger = logger
    private val loggingDeployExchanger: IOExchanger = object : IOExchanger {
        override fun listAsStrings(path: String): List<String> {
            gradleLogger.info("Requesting '$path' ...")
            return deployExchanger.listAsStrings(path)
        }

        override fun listAsUrls(path: String): List<URL> {
            gradleLogger.info("Requesting '$path' ...")
            return deployExchanger.listAsUrls(path)
        }

        override fun downloadFile(path: String): ByteArray {
            gradleLogger.info("Downloading '$path' ...")
            return deployExchanger.downloadFile(path)
        }

        override fun uploadFile(path: String, byteArray: ByteArray, override: Boolean) {
            gradleLogger.info("Uploading ${if (override) "updated " else ""}'$path' ...")
            deployExchanger.uploadFile(path, byteArray, override)
        }

        override fun deleteFile(path: String) {
            gradleLogger.info("Deleting '$path' ...")
            deployExchanger.deleteFile(path)
        }

        override fun getRootURI(): URI {
            gradleLogger.info("Requesting root URI ...")
            return deployExchanger.rootURI
        }
    }


    init {
        group = "Distribution"
        description = "Publishes Worder's artifacts, dependencies and descriptor to bintray. It's OS-dependent!"

        dependsOn(project.tasks.getByName("configJavafxRun"))
        dependsOn(project.tasks.getByName(JavaPlugin.JAR_TASK_NAME))
    }


    @TaskAction
    fun execute() {
        require(this::deployExchanger.isInitialized) {
            "Please make sure you have specified deployer object!"
        }

        val projectPath = project.projectDir.absolutePath
        val execTask = project.tasks.findByName(ApplicationPlugin.TASK_RUN_NAME) as JavaExec
        val allJvmArgs = execTask.allJvmArgs

        val worderFiles = (project.tasks.findByName(JavaPlugin.JAR_TASK_NAME) as Jar).outputs.files.files
                .map { it.toPath() }
        val modulePathFiles = findOption(allJvmArgs, "--module-path")
                .split(
                        when (OS.getCurrentOS()!!) {
                            OS.LINUX -> ":"
                            OS.WINDOWS_10 -> ";"
                        }
                )
                .filterNot { it.startsWith(projectPath) }
                .map { Path.of(it) }
        val classPathFiles = execTask.classpath
                .map { it.toPath() }
                .filterNot {
                    it.toString().startsWith(projectPath) || it.fileName.startsWith("javafx") &&
                            (it.fileName.endsWith("linux.jar") || it.fileName.endsWith("win.jar") || it.fileName.endsWith("mac.jar"))
                }
        // the temporary patch above is due to https://github.com/openjfx/javafx-gradle-plugin/issues/65


        with(if (gradleLogger.isInfoEnabled) loggingDeployExchanger else deployExchanger) {
            val remoteDescriptorNames = (listAsStrings("") ?: emptyList<String>())
                    .filter { it.startsWith("WorderAppDescriptor-") }
                    .toMutableSet()

            if (removeAllDescriptors) {
                remoteDescriptorNames.removeAll {
                    deleteFile(it)
                    true
                }
            }

            val newDescriptor = AppDescriptor.Builder()
                    .appVersion(project.version.toString())
                    .appMainClass(execTask.mainClass.get())
                    .usedModules(findOption(execTask.allJvmArgs, "--add-modules"))
                    .envArguments(allJvmArgs.filter { it.startsWith("-D") })
                    .modulePath(modulePathFiles)
                    .classPath(classPathFiles + worderFiles)
                    .build()

            uploadFile(newDescriptor.name, newDescriptor.toByteArray(), true)
            remoteDescriptorNames.remove(newDescriptor.name)

            val actualRemoteDescriptors = remoteDescriptorNames.map { AppDescriptor.fromByteArray(downloadFile(it)) } + newDescriptor
            val actualRemoteArtifacts = (listAsStrings("artifacts") ?: emptyList()).associateWithTo(mutableMapOf()) { 0 }
            val artifactsToUpload = mutableListOf<AppDescriptor.Artifact>()

            actualRemoteDescriptors
                    .flatMap { it.artifacts }
                    .forEach {
                        actualRemoteArtifacts.compute(it.name) { _, v ->
                            if (v == null) {
                                artifactsToUpload.add(it)
                                1
                            } else {
                                v + 1
                            }
                        }
                    }

            actualRemoteArtifacts
                    .filter { it.value == 0 }
                    .forEach { deleteFile("artifacts/${it.key}") }

            artifactsToUpload
                    .forEach { uploadFile("artifacts/${it.name}", Files.readAllBytes(it.pathToFile), false) }
        }
    }


    private fun findOption(allJvmArgs: List<String>, option: String): String {
        allJvmArgs.forEachIndexed { index, value ->
            if (value == option) {
                return allJvmArgs[index + 1]
            }
        }

        error("The requested option \"$option\" has not been found in JvmArguments: $allJvmArgs")
    }
}
