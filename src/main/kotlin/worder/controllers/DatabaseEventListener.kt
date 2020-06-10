package worder.controllers

import worder.model.database.WorderDB

interface DatabaseEventListener {
    fun onDatabaseConnection(db: WorderDB)
    fun onDatabaseDisconnection()
}
