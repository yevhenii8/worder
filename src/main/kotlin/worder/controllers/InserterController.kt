package worder.controllers

import tornadofx.Controller
import worder.views.InserterView
import java.io.File


class InserterController : Controller() {
    private val inserterView: InserterView by inject()
    private val databaseController: DatabaseController by inject()

    fun processFiles(files: List<File>) {
        TODO()
    }
}
