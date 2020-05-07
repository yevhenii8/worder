package worder.controllers

import tornadofx.Controller
import worder.model.AbstractStats
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

    val stats: AbstractStats = DatabaseControllerStats()

    class DatabaseControllerStats : AbstractStats() {
        override val origin: String = this.javaClass.simpleName

        var dbConnected: Boolean by map
        var dbFileName: String by map
        var dbFilePath: String by map

        init {
            map["dbConnected"] = false
            map["dbFileName"] = "undefined"
            map["dbFilePath"] = "undefined"
        }
    }
}
