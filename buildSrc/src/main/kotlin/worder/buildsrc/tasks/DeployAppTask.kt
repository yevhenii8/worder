package worder.buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.options.Option
import org.gradle.jvm.tasks.Jar
import worder.buildsrc.AppDeployer
import worder.commons.AppDescriptor
import java.nio.file.Files
import java.nio.file.Path

@Suppress("LeakingThis")
open class DeployAppTask : DefaultTask() {
    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var deployer: AppDeployer

    @Option(description = "Use this option when AppDescriptor structure changes.")
    var removeAllDescriptors = false


    val gradleLogger = logger
    val loggingDeployer = object : AppDeployer {
        override fun listCatalog(path: String): List<String> {
            gradleLogger.info("Requesting '$path' ...")
            return deployer.listCatalog(path)
        }

        override fun downloadFile(path: String): ByteArray {
            gradleLogger.info("Downloading '$path' ...")
            return deployer.downloadFile(path)
        }

        override fun uploadFile(path: String, byteArray: ByteArray, override: Boolean) {
            gradleLogger.info("Uploading ${if (override) "updated " else ""}'$path' ...")
            deployer.uploadFile(path, byteArray, override)
        }

        override fun deleteFile(path: String) {
            gradleLogger.info("Deleting '$path' ...")
            deployer.deleteFile(path)
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
        require(this::deployer.isInitialized) {
            "Please make sure you have specified deployer object!"
        }

        val projectPath = project.projectDir.absolutePath
        val execTask = project.tasks.findByName(ApplicationPlugin.TASK_RUN_NAME) as JavaExec
        val allJvmArgs = execTask.allJvmArgs

        val worderFiles = (project.tasks.findByName(JavaPlugin.JAR_TASK_NAME) as Jar).outputs.files.files
                .map { it.toPath() }
        val modulePathFiles = findOption(allJvmArgs, "--module-path")
                .split(":")
                .filterNot { it.startsWith(projectPath) }
                .map { Path.of(it) }
        val classPathFiles = execTask.classpath
                .map { it.toPath() }
                .filterNot { it.toString().startsWith(projectPath) || modulePathFiles.contains(it) }


        with(if (gradleLogger.isInfoEnabled) loggingDeployer else deployer) {
            val remoteDescriptorNames = listCatalog()
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

            uploadFile(newDescriptor.name, newDescriptor.toByteArray(), override = true)
            remoteDescriptorNames.remove(newDescriptor.name)

            val actualRemoteDescriptors = remoteDescriptorNames.map { AppDescriptor.fromByteArray(downloadFile(it)) } + newDescriptor
            val actualArtifacts = listCatalog("artifacts").associateTo(mutableMapOf()) { it to 0 }

            actualRemoteDescriptors
                    .flatMap { it.artifacts }
                    .forEach {
                        actualArtifacts.compute(it.name) { _, v ->
                            if (v == null) {
                                uploadFile("artifacts/${it.name}", Files.readAllBytes(it.pathToFile))
                                1
                            } else {
                                v + 1
                            }
                        }
                    }

            actualArtifacts
                    .filter { it.value == 0 }
                    .forEach { deleteFile("artifacts/${it.key}") }
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
