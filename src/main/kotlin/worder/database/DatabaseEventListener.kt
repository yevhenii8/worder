package worder.database

import worder.database.model.WorderDB

interface DatabaseEventListener {
    fun onDatabaseConnection(db: WorderDB)
    fun onDatabaseDisconnection()
}
