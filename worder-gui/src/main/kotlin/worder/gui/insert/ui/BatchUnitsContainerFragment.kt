/**
 * Stamp was generated by <StampedSourceFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <BatchUnitsContainerFragment.kt>
 * Created: <05/07/2020, 06:50:42 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <133>
 */

package worder.gui.insert.ui

import javafx.collections.ObservableList
import javafx.geometry.HorizontalDirection
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollBar
import javafx.scene.control.ScrollPane
import javafx.scene.layout.VBox
import tornadofx.Fragment
import tornadofx.bindComponents
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.onChange
import tornadofx.paddingAll
import tornadofx.paddingLeft
import tornadofx.paddingRight
import tornadofx.px
import tornadofx.scrollpane
import tornadofx.style
import tornadofx.vbox
import tornadofx.visibleWhen
import worder.gui.insert.model.BatchUnit

class BatchUnitsContainerFragment : Fragment() {
    private val units: ObservableList<BatchUnit> by param()
    private val direction: HorizontalDirection by param()
    private val scrollBarUI: ScrollBar = ScrollBar().apply {
        orientation = Orientation.VERTICAL
        visibleWhen {
            visibleAmountProperty().lessThan(1.0)
        }
    }
    private val unitsUI: VBox = vbox(spacing = 20, alignment = Pos.CENTER) {
        bindComponents(units) { unit ->
            find<BatchUnitFragment>("unit" to unit)
        }
    }
    private val scrollPaneUI: ScrollPane = scrollpane {
        content = if (units.isEmpty()) find<EmptyUnitsContainer>().root else unitsUI
        vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
        isFitToHeight = true

        scrollBarUI.apply {
            minProperty().bind(vminProperty())
            maxProperty().bind(vmaxProperty())
            visibleAmountProperty().bind(heightProperty().divide(unitsUI.heightProperty()))
            vvalueProperty().bindBidirectional(valueProperty())
        }
    }


    init {
        configureScrollPanePadding()
        units.onChange {
            when (it.list.size) {
                0 -> {
                    scrollPaneUI.content = find<EmptyUnitsContainer>().root
                    configureScrollPanePadding()
                }
                1 -> {
                    scrollPaneUI.content = unitsUI
                    configureScrollPanePadding()
                }
            }
        }
    }


    override val root = hbox {
        if (direction == HorizontalDirection.LEFT)
            add(scrollBarUI)

        add(scrollPaneUI)

        if (direction == HorizontalDirection.RIGHT)
            add(scrollBarUI)
    }


    private fun configureScrollPanePadding() {
        with(scrollPaneUI) {
            if (units.isEmpty()) {
                paddingAll = 0
                return
            }

            when (direction) {
                HorizontalDirection.LEFT -> paddingLeft = 15
                HorizontalDirection.RIGHT -> paddingRight = 15
            }
        }
    }


    class EmptyUnitsContainer : Fragment() {
        override val root: Parent = vbox(spacing = 15, alignment = Pos.CENTER) {
            prefWidth = BatchUnitFragment.batchUnitWidth + 15
            imageview("/images/empty-box.png")
            label("NO UNITS HERE").style { fontSize = 18.px }
        }
    }
}