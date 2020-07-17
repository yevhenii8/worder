/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DatabaseController.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <17/07/2020, 07:38:49 PM>
 * Version: <16>
 */

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
    val controllerStats: BaseObservableStats = BaseObservableStats(
            origin = "Database Controller",
            titlesOrderPriority = listOf(
                    "Data source",
                    "Database size",
                    "Session duration"
            )
    )

    private val listeners = mutableListOf<DatabaseEventListener>()
    private var timerValue: String by controllerStats.bindThroughValue(initValue = "00:00:00", propertyTitle = "Session duration")
    private var dbFileSize: String by controllerStats.bindThroughValue(initValue = "0 KiB", propertyTitle = "Database size")
    private var isConnected: Boolean = false
    private var currentDB: File? = null
    private var secTicker: Job? = null

    var db: WorderDB? by controllerStats.bindThroughValue(initValue = null, propertyTitle = "Data source")
        private set


    /*
    Public Controller's API
     */

    fun connectToSqlLiteFile(file: File) {
        currentDB = file
        dbFileSize = "${file.length() / 1024} KiB"
        connect(SQLiteFile.createInstance(file))
    }

    fun disconnect() {
        if (isConnected) {
            db = null
            isConnected = false
            secTicker?.cancel()
            currentDB = null
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
        secTicker = MainScope().launch { tickerJob() }
        listeners.forEach { it.onDatabaseConnection(db!!) }
    }

    private suspend fun tickerJob() {
        var seconds = 0
        while (true) {
            delay(1000L)
            seconds++

            val h = seconds / 3600
            val m = seconds % 3600 / 60
            val s = seconds % 60

            timerValue = "${if (h < 10) "0$h" else h.toString()}:${if (m < 10) "0$m" else m.toString()}:${if (s < 10) "0$s" else s.toString()}"
            dbFileSize = "${currentDB!!.length() / 1024} KiB"
        }
    }
}
