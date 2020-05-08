package worder.controllers

import tornadofx.Controller
import worder.model.SharedStats
import worder.model.database.WorderDB
import worder.model.database.WorderInsertDB
import worder.model.database.WorderUpdateDB
import worder.views.DatabaseView

class DatabaseController : Controller() {
    private val view: DatabaseView by inject()

    var db: WorderDB? = null
        private set

    var updateDb: WorderUpdateDB? = null
        private set

    var insertDb: WorderInsertDB? = null
        private set

    var dbFilePath: String by stats
        private set

    val stats = SharedStats(this.javaClass.simpleName)
    val map = mutableMapOf<String, Any>()

    var dbConnected: Boolean by stats
    var dbFileName: String by map
}
