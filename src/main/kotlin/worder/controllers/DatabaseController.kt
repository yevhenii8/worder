package worder.controllers

import tornadofx.Controller
import worder.views.DatabaseView

class DatabaseController : Controller() {
    val view: DatabaseView by inject()
}
