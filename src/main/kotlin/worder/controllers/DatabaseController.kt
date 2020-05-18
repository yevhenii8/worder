package worder.controllers

import javafx.util.Duration
import tornadofx.Controller
import tornadofx.ViewTransition
import worder.model.SharedStats
import worder.model.SharedStats.SharedStatsBinder
import worder.model.database.WorderDB
import worder.model.database.WorderInsertDB
import worder.model.database.WorderUpdateDB
import worder.views.ConnectionView
import worder.views.DisconnectionView

class DatabaseController : Controller() {
    private val connectionView: ConnectionView by inject()
    private val disconnectionView: DisconnectionView by inject()

    val stats = SharedStats(this.javaClass.simpleName)

    var db: WorderDB? by SharedStatsBinder.bind(stats, null)
        private set
    var updateDb: WorderUpdateDB? by SharedStatsBinder.bind(stats, null)
        private set
    var insertDb: WorderInsertDB? by SharedStatsBinder.bind(stats, null)
        private set
    var isConnected: Boolean by SharedStatsBinder.bind(stats, false)
        private set

    fun connectToSqlLiteFile(filePath: String) {
//        db = SqlLiteFile(filePath).also {
//            updateDb = it
//            insertDb = it
//        }

        isConnected = true
        connectionView.replaceWith<DisconnectionView>()
    }

    fun disconnect() {
        db = null
        updateDb = null
        insertDb = null

        isConnected = false
        disconnectionView.replaceWith<ConnectionView>()
    }
}
