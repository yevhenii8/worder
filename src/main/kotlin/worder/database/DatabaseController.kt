package worder.database

import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornadofx.Controller
import worder.core.model.BaseObservableStats
import worder.database.model.WorderDB
import worder.database.model.implementations.SQLiteFile
import java.io.File

class DatabaseController : Controller(), DatabaseEventProducer {
    val observableStats: BaseObservableStats = BaseObservableStats("Database Controller")

    var db: WorderDB? by observableStats.bindToStats(
            initValue = null,
            defaultTitle = "Data source"
    )
        private set

    var isConnected: Boolean by observableStats.bindToStats(
            initValue = false,
            defaultTitle = "Connected"
    )
        private set


    private val listeners = mutableListOf<DatabaseEventListener>()
    private var timerJob: Job? = null
    private var timerValue: String by observableStats.bindToStats(
            initValue = "00:00:00",
            defaultTitle = "Session duration"
    )


    /*
    Public Controller's API
     */

    fun connectToSqlLiteFile(file: File) = connect(SQLiteFile.createInstance(file))

    fun disconnect() {
        if (isConnected) {
            db = null
            isConnected = false
            timerJob?.cancel()
            listeners.forEach { it.onDatabaseDisconnection() }
        }
    }

    override fun subscribe(eventListener: DatabaseEventListener) {
        listeners.add(eventListener)
    }

    override fun subscribeAndRaise(eventListener: DatabaseEventListener) {
        subscribe(eventListener)

        if (isConnected)
            eventListener.onDatabaseConnection(db!!)
        else
            eventListener.onDatabaseDisconnection()
    }


    /*
    Inner Private Methods
     */

    private fun connect(otherDB: WorderDB) {
        if (isConnected)
            disconnect()

        db = otherDB
        isConnected = true
        timerJob = MainScope().launch { clockUpdater() }
        listeners.forEach { it.onDatabaseConnection(db!!) }
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