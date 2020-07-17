/**
 * Stamp was generated by <StampedSourceFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <InsertUnitsContainerFragment.kt>
 * Created: <05/07/2020, 06:50:42 PM>
 * Modified: <17/07/2020, 02:44:24 PM>
 * Version: <63>
 */

package worder.insert.view

import javafx.collections.ObservableList
import javafx.geometry.HorizontalDirection
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.control.ScrollBar
import javafx.scene.control.ScrollPane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.bindComponents
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.label
import tornadofx.onChange
import tornadofx.paddingLeft
import tornadofx.paddingRight
import tornadofx.paddingTop
import tornadofx.scrollpane
import tornadofx.vbox
import tornadofx.visibleWhen
import worder.core.styles.WorderDefaultStyles
import worder.insert.model.InsertUnit

class InsertUnitsContainerFragment : Fragment() {
    private val units: ObservableList<InsertUnit> by param()
    private val direction: HorizontalDirection by param()
    private val scrollBar: ScrollBar = ScrollBar().apply {
        orientation = Orientation.VERTICAL
        visibleWhen {
            visibleAmountProperty().lessThan(1.0)
        }
    }
    private val unitsUI: VBox = vbox(spacing = 20) {
        bindComponents(units) { unit ->
            find<InsertUnitFragment>("unit" to unit)
        }
    }


    override val root = hbox {
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE)

        if (direction == HorizontalDirection.LEFT) {
            paddingRight = scrollBar.width
            add(scrollBar)
        }

        val scrollPane = scrollpane {
            addClass(WorderDefaultStyles.insertUnits)

            content = if (units.isEmpty()) find<EmptyUnitsContainer>().root else unitsUI
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            alignment = Pos.CENTER

            scrollBar.apply {
                minProperty().bind(vminProperty())
                maxProperty().bind(vmaxProperty())
                visibleAmountProperty().bind(heightProperty().divide(unitsUI.heightProperty()))
                vvalueProperty().bindBidirectional(valueProperty())
            }
        }

        if (direction == HorizontalDirection.RIGHT) {
            paddingLeft = scrollBar.width
            add(scrollBar)
        }

        unitsUI.children.onChange {
            when (it.list.size) {
                0 -> scrollPane.content = find<EmptyUnitsContainer>().root
                1 -> scrollPane.content = unitsUI
            }
        }
    }


    class EmptyUnitsContainer : Fragment() {
        override val root: Parent = vbox {
            alignment = Pos.CENTER
            spacing = 15.0
            paddingTop = 200
            imageview("/images/empty-box.png")
            label("NO UNITS HERE :(")
        }
    }
}
