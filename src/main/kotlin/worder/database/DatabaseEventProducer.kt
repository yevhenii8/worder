package worder.database

import worder.database.DatabaseEventListener

interface DatabaseEventProducer {
    fun subscribe(eventListener: DatabaseEventListener)
    fun subscribeAndRaise(eventListener: DatabaseEventListener)
}
