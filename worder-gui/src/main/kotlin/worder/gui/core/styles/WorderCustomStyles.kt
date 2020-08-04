/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WorderCustomStyles.kt>
 * Created: <02/08/2020, 04:33:41 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <18>
 */

package worder.gui.core.styles

import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.paint.Color
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.c
import tornadofx.cssclass
import tornadofx.cssid
import tornadofx.mixin
import tornadofx.px

class WorderCustomStyles : Stylesheet() {
    companion object {
        val worderBlock by cssclass()
        val dragDropField by cssclass()
        val batchStatBlock by cssclass()

        val batchProgress by cssid()
    }


    init {
        val defaultWorderBlock = mixin {
            borderColor += box(Color.GRAY)
            borderRadius += box(10.px)
            padding = box(10.px)
        }

        worderBlock {
            +defaultWorderBlock
        }

        batchProgress {
            prefWidth = 100.px
            prefHeight = 120.px
            percentage {
                fontSize = 20.px
            }
        }

        batchStatBlock {
            +defaultWorderBlock
            padding = box(20.px)
            label {
                padding = box(5.px, 0.px)
            }
        }

        dragDropField {
            backgroundColor += c(Color.GRAY.toString(), 0.1)
            borderColor += box(Color.DARKGRAY)
            borderStyle += BorderStrokeStyle(
                    StrokeType.OUTSIDE,
                    StrokeLineJoin.MITER,
                    StrokeLineCap.BUTT,
                    10.0,
                    0.0,
                    listOf(20.0, 14.0)
            )
        }
    }
}