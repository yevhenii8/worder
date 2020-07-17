/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <ListBasedStatsFragment.kt>
 * Created: <10/07/2020, 09:03:31 PM>
 * Modified: <17/07/2020, 10:50:37 PM>
 * Version: <15>
 */

package worder.core.view

import javafx.beans.property.ReadOnlyProperty
import javafx.geometry.Pos
import javafx.util.StringConverter
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.label
import tornadofx.paddingBottom
import tornadofx.vbox
import worder.core.styles.WorderDefaultStyles

class ListBasedStatsFragment : Fragment() {
    private val nameMutators: Map<String, String.() -> String>? by param()
    private val valueMutators: Map<String, Any?.() -> String>? by param()

    private val commonNameMutator: (String.() -> String)? by param()
    private val commonValueMutator: (Any?.() -> String)? by param()

    private val stats: List<ReadOnlyProperty<*>> by param()
    private val blockTitle: String? by param()

    private val nameConverter = when {
        nameMutators != null && commonNameMutator != null -> { name: String ->
            val common = commonNameMutator!!.invoke(name)
            val own = nameMutators!![name]?.invoke(common) ?: common
            "$own: "
        }
        nameMutators != null -> { name: String ->
            "${nameMutators!![name]?.invoke(name) ?: name}: "
        }
        commonNameMutator != null -> { name: String ->
            "${commonNameMutator!!.invoke(name)}: "
        }
        else -> { name: String ->
            "$name: "
        }
    }
    private val valueConverter = when {
        valueMutators != null && commonValueMutator != null -> { name: String, value: Any? ->
            val common = commonValueMutator!!.invoke(value)
            val own = valueMutators!![name]?.invoke(value) ?: common
            own
        }
        valueMutators != null -> { name: String, value: Any? ->
            "${valueMutators!![name]?.invoke(value) ?: value}"
        }
        commonValueMutator != null -> { _: String, value: Any? ->
            commonValueMutator!!.invoke(value)
        }
        else -> { _: String, value: Any? ->
            "$value"
        }
    }


    override val root = vbox {
        if (blockTitle != null)
            label(blockTitle!!) {
                paddingBottom = 15
            }

        hbox {
            vbox(alignment = Pos.BASELINE_RIGHT) {
                stats.forEach {
                    label(nameConverter.invoke(it.name))
                }
            }

            vbox(alignment = Pos.BASELINE_LEFT) {
                @Suppress("UNCHECKED_CAST")
                (stats as List<ReadOnlyProperty<Any?>>).forEach {
                    label(
                            observable = it,
                            converter = object : StringConverter<Any?>() {
                                override fun toString(obj: Any?): String = valueConverter.invoke(it.name, it.value)
                                override fun fromString(string: String?): Any? = throw IllegalStateException()
                            }
                    )
                }
            }
        }

        addClass(WorderDefaultStyles.statsBlock)
    }
}
