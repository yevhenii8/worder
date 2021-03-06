/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <MapBasedStatsFragment.kt>
 * Created: <10/07/2020, 09:03:31 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <55>
 */

package worder.gui.core.ui

import javafx.collections.ObservableMap
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.hgrow
import tornadofx.label
import tornadofx.style
import tornadofx.vbox
import worder.gui.core.styles.WorderCustomStyles
import worder.gui.tornadofx.bindChildren

class MapBasedStatsFragment : Fragment() {
    private val nameMutators: Map<String, String.() -> String>? by param()
    private val commonNameMutator: (String.() -> String)? by param()

    private val valueMutators: Map<String, Any?.() -> String>? by param()
    private val commonValueMutator: (Any?.() -> String)? by param()

    private val blockTitle: String? by param()
    private val stats: ObservableMap<String, Any?> by param()
    private val hideNullable: Boolean by param()

    private val nameConverter = when {
        nameMutators != null && commonNameMutator != null -> { name: String, _: Any? ->
            val common = commonNameMutator!!.invoke(name)
            val own = nameMutators!![name]?.invoke(common) ?: common
            label(own)
        }
        nameMutators != null -> { name: String, _: Any? ->
            label(nameMutators!![name]?.invoke(name) ?: name)
        }
        commonNameMutator != null -> { name: String, _: Any? ->
            label(commonNameMutator!!.invoke(name))
        }
        else -> { name: String, _: Any? ->
            label(name)
        }
    }
    private val valueConverter = when {
        valueMutators != null && commonValueMutator != null -> { name: String, value: Any? ->
            val common = commonValueMutator!!.invoke(value)
            val own = valueMutators!![name]?.invoke(value) ?: common
            label(own)
        }
        valueMutators != null -> { name: String, value: Any? ->
            label("${valueMutators!![name]?.invoke(value) ?: value}")
        }
        commonValueMutator != null -> { _: String, value: Any? ->
            label(commonValueMutator!!.invoke(value))
        }
        else -> { _: String, value: Any? ->
            label("$value")
        }
    }


    override val root: VBox = vbox(spacing = 5) {
        addClass(WorderCustomStyles.worderBlock)
        style(append = true) { alignment = Pos.TOP_CENTER }

        if (blockTitle != null)
            label(blockTitle!!)

        hbox(spacing = 30) {
            vbox(alignment = Pos.BASELINE_LEFT) {
                hgrow = Priority.ALWAYS
                if (hideNullable)
                    bindChildren(this@MapBasedStatsFragment.stats, nameConverter, { _, value -> value != null })
                else
                    bindChildren(this@MapBasedStatsFragment.stats, nameConverter)
            }
            vbox(alignment = Pos.BASELINE_LEFT) {
                if (hideNullable)
                    bindChildren(this@MapBasedStatsFragment.stats, valueConverter, { _, value -> value != null })
                else
                    bindChildren(this@MapBasedStatsFragment.stats, valueConverter)
            }
        }
    }
}
