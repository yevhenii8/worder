package worder.views.fragments

import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.bindChildren
import tornadofx.label
import tornadofx.paddingBottom
import tornadofx.vbox
import worder.model.ObservableStats
import worder.views.styles.WorderStyle

class ObservableStatsFragment : Fragment() {
    private val observableStats: ObservableStats by param()

    override val root = vbox {
        label(observableStats.origin) {
            paddingBottom = 15
        }

        bindChildren(observableStats.asTitledMapProperty) { title, value ->
            label("$title: $value")
        }

        addClass(WorderStyle.statBlock)
    }
}
