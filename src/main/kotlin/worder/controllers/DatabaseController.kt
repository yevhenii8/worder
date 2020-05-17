package worder.controllers

import tornadofx.Controller
import worder.model.SharedStats
import worder.model.SharedStats.SharedStatsBinder
import worder.model.database.WorderDB
import worder.model.database.WorderInsertDB
import worder.model.database.WorderUpdateDB
import worder.views.DatabaseView

class DatabaseController : Controller() {
    private val view: DatabaseView by inject()


    val stats = SharedStats(this.javaClass.simpleName)


    var db: WorderDB? by SharedStatsBinder.bind(stats, null)
        private set
    var updateDb: WorderUpdateDB? by SharedStatsBinder.bind(stats, null)
        private set
    var insertDb: WorderInsertDB? by SharedStatsBinder.bind(stats, null)
        private set
    var dbConnected: Boolean by SharedStatsBinder.bind(stats, false)
        private set
}
