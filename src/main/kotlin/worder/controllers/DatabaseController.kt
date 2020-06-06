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
import java.io.File

class DatabaseController : Controller() {
    val stats = SharedStats("Database Controller")


    private val listeners = mutableListOf<DatabaseListener>()
    private var timerValue: String by SharedStatsBinder.bind(stats, "00:00:00")
    private var timerJob: Job? = null


    var db: WorderDB? by SharedStatsBinder.bind(stats, null)
        private set

    var isConnected: Boolean by SharedStatsBinder.bind(stats, false)
        private set


    /*
    Public Controller's API
     */

    fun connectToSqlLiteFile(file: File) {
        db = SqlLiteFile.createInstance(file)
        connect()
    }

    fun disconnect() {
        db = null
        isConnected = false
        timerJob?.cancel()
        listeners.forEach { it.onDatabaseDisconnection() }
    }

    fun subscribe(listener: DatabaseListener) {
        listeners.add(listener)
    }


    /*
    Inner Private Methods
     */

    private fun connect() {
        isConnected = true
        timerJob = MainScope().launch { clockUpdater() }
        listeners.forEach { it.onDatabaseConnection() }
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
