package worder.core.view

import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.label
import tornadofx.paddingBottom
import tornadofx.vbox
import worder.core.model.ObservableStats
import worder.tornadofx.bindChildren

class ObservableStatsFragment : Fragment() {
    private val observableStats: ObservableStats by param()

    override val root = vbox {
        label(observableStats.origin) {
            paddingBottom = 15
        }

        vbox {
            bindChildren(observableStats.asTitledMapProperty) { title, value ->
                label("$title: $value")
            }
        }

        addClass(WorderBrightStyles.statBlock)
    }
}
