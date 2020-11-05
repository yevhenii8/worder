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

    @Option(description = "Run the task in the Transit mode. Use when updating a descriptor structure.")
    var onlyDescriptor = false


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

        val gradleLogger = logger
        val execTask = project.tasks.findByName(ApplicationPlugin.TASK_RUN_NAME) as JavaExec
        val appArtifacts = (project.tasks.findByName(JavaPlugin.JAR_TASK_NAME) as Jar).outputs.files.files.map { it.toPath() }
        val projectPath = project.projectDir.absolutePath
        val allJvmArgs = execTask.allJvmArgs

        val modulePath = findOption(allJvmArgs, "--module-path")
                .split(":")
                .filterNot { it.startsWith(projectPath) }
                .map { Path.of(it) }
        val classPath = execTask.classpath
                .map { it.toPath() }
                .filterNot { it.startsWith(projectPath) || modulePath.contains(it) }

        with(deployer) {
            gradleLogger.info("Deploying to $this has been started!")

            val remoteDescriptors = listCatalog()
                    .filter { it.startsWith("WorderAppDescriptor-") }
                    .map { AppDescriptor.fromByteArray(downloadFile(it)) }
            val previousDescriptor = remoteDescriptors.find { it.name == AppDescriptor.getCalculatedName() }
            val remoteArtifacts = remoteDescriptors
                    .flatMap { it.allArtifacts }
                    .groupBy({ it.name }, { 1 })
                    .mapValues { it.value.size }
                    .toMutableMap()
            val newDescriptor = AppDescriptor.Builder()
                    .appVersion(project.version.toString())
                    .appMainClass(execTask.mainClass.get())
                    .usedModules(findOption(execTask.allJvmArgs, "--add-modules"))
                    .envArguments(allJvmArgs.filter { it.startsWith("-D") })
                    .modulePath(modulePath)
                    .classPath(classPath + appArtifacts)
                    .version(previousDescriptor?.version?.plus(1) ?: 1)
                    .build()

            newDescriptor.allArtifacts.forEach {
                if (!remoteArtifacts.contains(it.name)) {
                    gradleLogger.info("Uploading ${it.name}")
                    uploadFile("artifacts/${it.name}", Files.readAllBytes(it.pathToFile))
                }
            }

            previousDescriptor?.let { previous: AppDescriptor ->
                (previous.allArtifacts subtract newDescriptor.allArtifacts).forEach {
                    if (remoteArtifacts[it.name]!! - 1 == 0) {
                        gradleLogger.info("Removing ${it.name}")
                        removeFile("artifacts/${it.name}")
                    }
                }
            }

            gradleLogger.info("Uploading${previousDescriptor?.let { " updated " } ?: " "}${newDescriptor.name}")
            uploadFile(newDescriptor.name, newDescriptor.toByteArray(), override = true)
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
