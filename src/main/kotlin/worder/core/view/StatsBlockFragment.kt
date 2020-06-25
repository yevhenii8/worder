package worder.core.view

import javafx.geometry.Pos
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.label
import tornadofx.vbox
import worder.core.styles.WorderDefaultStyles

@Deprecated("Use ObservableStatsFragment instead", level = DeprecationLevel.HIDDEN)
@Suppress("DEPRECATION_ERROR")
class StatsBlockFragment : Fragment() {
    private val prettyNames: Map<String, String> by param()
    private val stats: worder.core.model.Stats by param()

    private val values = vbox(alignment = Pos.BASELINE_LEFT)
    private val names = vbox(alignment = Pos.BASELINE_RIGHT) {
        addClass(WorderDefaultStyles.names)
    }

    override val root = vbox {
        label(stats.origin) {
            addClass(WorderDefaultStyles.title)
        }

        hbox {
            add(names)
            add(values)
        }

        addClass(WorderDefaultStyles.statBlock)
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
