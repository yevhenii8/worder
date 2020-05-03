package worder.controllers

import tornadofx.Controller
import worder.views.DashboardView

class DashboardController : Controller() {
    val view: DashboardView by inject()
}
