package worder.buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.tasks.Jar
import worder.buildsrc.ApplicationDescriptor
import worder.buildsrc.ApplicationDeployer
import java.io.File

@Suppress("LeakingThis")
open class DeployApplicationTask : DefaultTask() {
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
        val jarTask = project.tasks.findByName(JavaPlugin.JAR_TASK_NAME) as Jar
        val projectPath = project.projectDir.absolutePath
        val allJvmArgs = mutableListOf<String>().apply {
            addAll(execTask.allJvmArgs)
        }

        val newDescriptor = generateNewDescriptor(allJvmArgs, projectPath, jarTask.outputs.files.files)

        with(deployer) {
            uploadFile("$newDescriptor", newDescriptor.toJson().toByteArray())

            newDescriptor.modulePath.forEach {
                uploadFile(it.path, it.file!!)
            }

            newDescriptor.classPath.forEach {
                uploadFile(it.path, it.file!!)
            }
        }
    }


    private fun generateNewDescriptor(allJvmArgs: MutableList<String>, projectPath: String, appArtifacts: Collection<File>): ApplicationDescriptor {
        val usedModules: String = findOption(allJvmArgs, "--add-modules")
        val envArguments: List<String> = findEnvArguments(allJvmArgs)
        val mainClass: String = allJvmArgs.last()

        val modulePath: List<ApplicationDescriptor.Library> = findOption(allJvmArgs, "--module-path")
                .split(":")
                .filterNot { it.startsWith(projectPath) }
                .map { ApplicationDescriptor.Library(File(it)) }

        val classPath: List<ApplicationDescriptor.Library> = findOption(allJvmArgs, "-cp")
                .split(":")
                .filterNot { it.startsWith(projectPath) }
                .map { ApplicationDescriptor.Library(File(it)) }

        check(allJvmArgs.size == 1) {
            allJvmArgs.forEach { println(it) }
            "Unprocessed JVMArgs occurred! Please check it!"
        }

        return ApplicationDescriptor(
                mainClass = mainClass,
                envArguments = envArguments,
                usedModules = usedModules,
                modulePath = modulePath,
                classPath = classPath + appArtifacts.map { ApplicationDescriptor.Library(it) }
        )
    }

    private fun findOption(allJvmArgs: MutableList<String>, option: String): String {
        allJvmArgs.forEachIndexed { index, value ->
            if (value == option) {
                val res = allJvmArgs[index + 1]
                allJvmArgs.removeAt(index)
                allJvmArgs.removeAt(index)
                return res
            }
        }

        error("The requested option has not been found in JvmArguments: $option")
    }

    private fun findEnvArguments(allJvmArgs: MutableList<String>): List<String> {
        val res = allJvmArgs.filter { it.startsWith("-D") }
        allJvmArgs.removeIf { it.startsWith("-D") }
        return res
    }
}
