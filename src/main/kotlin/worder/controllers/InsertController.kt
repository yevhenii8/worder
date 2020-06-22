package worder.controllers

import tornadofx.Controller
import worder.model.insert.InsertModel
import worder.model.insert.implementations.DefaultInsertModel
import worder.views.InsertView
import java.io.File

class InsertController : Controller() {
    private val insertView: InsertView by inject()
    private val databaseController: DatabaseController by inject()

    var insertModel: InsertModel? = null


    fun uploadFiles(files: List<File>) {
        check(databaseController.isConnected) {
            "No database connection established!"
        }

        databaseController.db?.let {
            insertModel = DefaultInsertModel.createInstance(it.inserter, files)
            insertView.showUploadedView()
        }
    }
}
