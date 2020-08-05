package worder.buildsrc.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.plugins.ApplicationPlugin
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

@Suppress("LeakingThis")
open class DeployToBintrayTask : DefaultTask() {
    init {
        group = "Distribution"
        description = "Publishes Worder's artifacts, dependencies and descriptor to bintray. It's OS-dependent!"

        dependsOn(project.tasks.getByName("configJavafxRun"))
    }


    @TaskAction
    fun execute() {
        val execTask = project.tasks.findByName(ApplicationPlugin.TASK_RUN_NAME) as JavaExec
        println(execTask.allJvmArgs) // exactly what I need!
    }
}
