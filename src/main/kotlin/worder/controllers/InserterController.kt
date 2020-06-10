package worder.controllers

import tornadofx.Controller
import worder.model.insert.InsertModel
import worder.model.insert.implementations.SimpleInsertModel
import worder.views.InserterView
import java.io.File

class InserterController : Controller() {
    private val inserterView: InserterView by inject()
    private val databaseController: DatabaseController by inject()

    var insertModel: InsertModel? = null


    fun uploadFiles(files: List<File>) {
        if (!databaseController.isConnected)
            throw IllegalStateException("No database connection established!")

        databaseController.db?.let {
            insertModel = SimpleInsertModel.createInstance(it.inserter)
            insertModel!!.generateUnits(files)

            inserterView.showUploadedView()
        }
    }
}
