package worder.controllers

interface DatabaseEventProducer {
    fun subscribe(eventListener: DatabaseEventListener)
    fun subscribeAndRaise(eventListener: DatabaseEventListener)
}
