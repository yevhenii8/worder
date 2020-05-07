package worder.views

import javafx.scene.text.Text
import tornadofx.Fragment
import tornadofx.addChildIfPossible
import tornadofx.gridpane
import tornadofx.row
import tornadofx.text
import worder.model.Stats

class StatBlockFragment : Fragment() {
    private val stats: Stats by param()

    override val root = gridpane {
        row { text(stats.origin) }
    }

    init {
        stats.asMap.forEach { (name, value) ->
            val uiText = Text(value.toString())
            root.row(name) { addChildIfPossible(uiText) }
            stats.subscribe(name) { newValue -> uiText.text = newValue }
        }
    }
}
