package worder.buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import worder.buildsrc.ApplicationDeployer
import java.io.File
import java.nio.charset.Charset

@Suppress("LeakingThis")
open class DeployApplicationTask : DefaultTask() {
    @Suppress("MemberVisibilityCanBePrivate")
    lateinit var deployer: ApplicationDeployer


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

        val execTask = project.tasks.findByName(ApplicationPlugin.TASK_RUN_NAME) as JavaExec
        val appArtifacts = (project.tasks.findByName(JavaPlugin.JAR_TASK_NAME) as Jar).outputs.files.files
        val projectPath = project.projectDir.absolutePath
        val allJvmArgs = execTask.allJvmArgs

//        val modulePath = findOption(allJvmArgs, "--module-path")
//                .split(":")
//                .filterNot { it.startsWith(projectPath) }
//                .map { ApplicationDescriptor.Artifact(File(it)) }
//        val classPath = execTask.classpath
//                .filterNot { it.path.startsWith(projectPath) }
//                .map { ApplicationDescriptor.Artifact(it) }
//                .filterNot { modulePath.contains(it) }
//
//        with(deployer) {
//            val availableDescriptors = listCatalog()
//                    .filter { it.startsWith("WorderAppDescriptor-") && it.endsWith(".json") }
//                    .map { ApplicationDescriptor.fromJson(downloadFile(it).toString(Charset.defaultCharset())) }
//            val availableArtifacts = availableDescriptors
//                    .flatMap { it.allArtifacts }
//                    .groupBy({ it.name }, { 1 })
//                    .mapValues { it.value.size }
//                    .toMutableMap()
//            val generatedDescriptor = ApplicationDescriptor(
//                    appVersion = project.version.toString(),
//                    mainClass = execTask.mainClass.get(),
//                    envArguments = allJvmArgs.filter { it.startsWith("-D") },
//                    usedModules = findOption(execTask.allJvmArgs, "--add-modules"),
//                    modulePath = modulePath,
//                    classPath = classPath + appArtifacts.map { ApplicationDescriptor.Artifact(it) }
//            )
//
//            generatedDescriptor.allArtifacts.forEach {
//                if (!availableArtifacts.contains(it.name)) {
//                    uploadFile(it.name, it.file!!.readBytes())
//                }
//            }
//
//            availableDescriptors.find { it.OS == generatedDescriptor.OS }?.let { previous: ApplicationDescriptor ->
//                (previous.allArtifacts subtract generatedDescriptor.allArtifacts).forEach {
//                    if (availableArtifacts[it.name]!! - 1 == 0) {
//                        removeFile(it.name)
//                    }
//                }
//            }
//
//            uploadFile("$generatedDescriptor", generatedDescriptor.toJson().toByteArray(), override = true)
//        }
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
