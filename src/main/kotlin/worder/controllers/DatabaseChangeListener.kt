package worder.controllers

interface DatabaseChangeListener {
    fun onDatabaseConnection()
    fun onDatabaseDisconnection()
}
