package worder.controllers

import tornadofx.Controller
import worder.model.insert.implementations.BaseInsertModel
import worder.views.InserterView
import java.io.File

class InserterController : Controller() {
    private val inserterView: InserterView by inject()
    private val databaseController: DatabaseController by inject()

//    lateinit var insertBatch: InsertBatch


    fun processFiles(files: List<File>) {
        val database = databaseController.db!!.inserter
        val insertModel = BaseInsertModel.createInstance(database)
//        insertBatch = insertModel.prepareBatch(files)
        inserterView.displayBatch()
    }
}
