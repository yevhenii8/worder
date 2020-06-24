package worder.insert

import tornadofx.Controller
import worder.database.DatabaseController
import worder.database.DatabaseEventListener
import worder.database.model.WorderDB
import worder.insert.model.InsertModel
import worder.insert.model.implementations.DefaultInsertModel
import worder.insert.view.InsertView
import java.io.File

class InsertController : Controller() {
    private val insertView: InsertView by inject()
    private val databaseController: DatabaseController by inject()

    var currentInsertModel: InsertModel? = null


    fun generateInsertModel(files: List<File>) {
        currentInsertModel = DefaultInsertModel.createInstance(databaseController.db!!.inserter, files)
        insertView.toUploadedState()
    }

    fun releaseInsertModel() {
        currentInsertModel = null
        insertView.toNotUploadedState()
    }
}
