package worder.views

import tornadofx.View
import tornadofx.addClass
import tornadofx.hbox
import worder.views.styles.WorderStyle

class DashboardView : View() {
    override val root = hbox {
        addClass(WorderStyle.dashboard)
    }
}
