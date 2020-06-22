package worder.views.fragments

import javafx.geometry.Pos
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.label
import tornadofx.vbox
import worder.model.Stats
import worder.views.styles.WorderStyle

@Deprecated("Use ObservableStatsFragment instead", level = DeprecationLevel.HIDDEN)
class StatsBlockFragment : Fragment() {
    private val prettyNames: Map<String, String> by param()
    private val stats: Stats by param()

    private val values = vbox(alignment = Pos.BASELINE_LEFT)
    private val names = vbox(alignment = Pos.BASELINE_RIGHT) {
        addClass(WorderStyle.names)
    }

    override val root = vbox {
        label(stats.origin) {
            addClass(WorderStyle.title)
        }

        hbox {
            add(names)
            add(values)
        }

        addClass(WorderStyle.statBlock)
    }

    init {
        stats.asMap.forEach { (name, value) ->
            names.add(label("${prettyNames[name] ?: name}:"))
            values.add(
                    label(value.toString()).also {
                        stats.subscribe(name) { newValue -> it.text = newValue.toString() }
                    })
        }
    }
}
