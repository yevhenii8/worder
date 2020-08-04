/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WorderDefaultStyles.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <201>
 */

package worder.gui.core.styles

import javafx.geometry.Pos
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.paint.Color
import tornadofx.DrawerStyles.Companion.buttonArea
import tornadofx.DrawerStyles.Companion.drawer
import tornadofx.DrawerStyles.Companion.drawerItem
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.c
import tornadofx.px

class WorderDefaultStyles : Stylesheet() {
    init {
        text {
            fontSize = 12.px
        }

        imageView and disabled {
            opacity = 0.2
        }

        scrollPane {
            backgroundColor += Color.WHITE
            viewport {
                backgroundColor += Color.WHITE
            }
        }

        form {
            alignment = Pos.CENTER
            backgroundColor += Color.WHITE

            fieldset {
                alignment = Pos.CENTER
                padding = box(0.px)
                button {
                    padding = box(6.px)
                }
            }
        }

        titledPane {
            title {
                padding = box(8.px)
                backgroundInsets += box(0.px)
                backgroundRadius += box(0.px)
            }
        }

        button {
            padding = box(8.px)
            backgroundInsets += box(0.px)
            backgroundRadius += box(0.px)
        }

        comboBox {
            backgroundInsets += box(0.px)
            backgroundRadius += box(0.px)
        }

        drawer {
            backgroundImage += javaClass.getResource("/images/airplane.png").toURI()!!
            backgroundRepeat += BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT
            backgroundPosition += BackgroundPosition.CENTER

            drawerItem {
                backgroundColor += Color.WHITE
                padding = box(20.px)
            }

            buttonArea {
                backgroundColor += c("#f5f5f5")
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