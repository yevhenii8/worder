/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WorderDefaultStyles.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <16/07/2020, 09:42:44 PM>
 * Version: <90>
 */

package worder.core.styles

import javafx.geometry.Pos
import javafx.scene.effect.DropShadow
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.paint.Color
import tornadofx.DrawerStyles.Companion.buttonArea
import tornadofx.DrawerStyles.Companion.drawer
import tornadofx.DrawerStyles.Companion.drawerItem
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.c
import tornadofx.cssclass
import tornadofx.px

class WorderDefaultStyles : Stylesheet() {
    companion object {
        val statsBlock by cssclass()
        val insertUnit by cssclass()
        val insertUnits by cssclass()
        val insertModel by cssclass()
        val unitButton by cssclass()

        val dragDropField by cssclass()
        val statusBar by cssclass()
    }


    init {
        text {
            fontSize = 14.px
        }

        imageView and Stylesheet.disabled {
            opacity = 0.2
        }

        separator {
            line {
                effect = DropShadow()
            }
        }

        button {
            padding = box(8.px)
            backgroundInsets += box(0.px)
            backgroundRadius += box(0.px)
        }

        statsBlock {
            alignment = Pos.TOP_CENTER
            padding = box(15.px)
            borderRadius += box(15.px)
            borderColor += box(Color.GRAY)
        }

        insertUnits {
            backgroundColor += Color.TRANSPARENT

            insertUnit {
                padding = box(10.px)
                borderColor += box(Color.GRAY)
            }
        }

        insertModel {
            text {
                fontSize = 20.px
            }
        }

        dragDropField {
            backgroundColor += c(Color.GRAY.toString(), 0.1)
        }

        drawer {
            backgroundImage += javaClass.getResource("/images/airplane.png").toURI()!!
            backgroundRepeat += BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT
            backgroundPosition += BackgroundPosition(null, 600.0, false, null, 50.0, false)

            drawerItem {
                backgroundColor += c("#f4f4f4")
            }

            buttonArea {
                toggleButton {
                    padding = box(8.px)
                }
                text {
                    fontSize = 18.px
                }
            }
        }
    }
}
