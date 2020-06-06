package worder.controllers

interface DatabaseListener {
    fun onDatabaseConnection()
    fun onDatabaseDisconnection()
}
