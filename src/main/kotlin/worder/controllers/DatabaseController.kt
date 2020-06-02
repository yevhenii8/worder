package worder.controllers

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornadofx.Controller
import worder.model.SharedStats
import worder.model.SharedStats.SharedStatsBinder
import worder.model.database.WorderDB
import worder.model.database.implementations.SqlLiteFile
import worder.views.DatabaseView

class DatabaseController : Controller() {
    val stats = SharedStats("Database Controller")


    private val databaseView: DatabaseView by inject()
    private var timerValue: String by SharedStatsBinder.bind(stats, "00:00:00")
    private var timerJob: Job? = null


    var db: WorderDB? by SharedStatsBinder.bind(stats, null)
        private set

    var isConnected: Boolean by SharedStatsBinder.bind(stats, false)
        private set


    /*
    Public Controller's API
     */

    fun connectToSqlLiteFile(filePath: String) {
        db = SqlLiteFile.createInstance(filePath.removeSurrounding("[", "]"))
        connect()
    }

    fun disconnect() {
        db = null
        isConnected = false
        databaseView.onDisconnect()
        timerJob?.cancel()

    }


    /*
    Inner Private Methods
     */

    private fun connect() {
        isConnected = true
        databaseView.onConnect()
        timerJob = MainScope().launch { clockUpdater() }
    }

    private suspend fun clockUpdater() {
        var seconds = 0
        while (true) {
            delay(1000L)
            seconds++

            val h = seconds / 3600
            val m = seconds % 3600 / 60
            val s = seconds % 60

            timerValue = "${if (h < 10) "0$h" else h.toString()}:${if (m < 10) "0$m" else m.toString()}:${if (s < 10) "0$s" else s.toString()}"
        }
    }
}
