package worder.core.styles

import javafx.geometry.Pos
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.paint.Color
import tornadofx.DrawerStyles.Companion.drawer
import tornadofx.DrawerStyles.Companion.drawerItem
import tornadofx.SqueezeBoxStyles.Companion.squeezeBox
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.c
import tornadofx.cssclass
import tornadofx.infinity
import tornadofx.px

class WorderDefaultStyles : Stylesheet() {
    companion object {
        val statBlock by cssclass()
        val title by cssclass()
        val names by cssclass()

        val insertUnit by cssclass()
        val insertUnits by cssclass()
        val invalidWords by cssclass()
        val unitButtons by cssclass()

        val dragDropField by cssclass()
        val statusBar by cssclass()
    }

    init {
        s(text, label) {
            fontSize = 16.px
        }

        statBlock {
            alignment = Pos.TOP_CENTER
            padding = box(15.px)
            borderInsets += box(15.px)
            borderRadius += box(15.px)
            borderColor += box(Color.GRAY)

            title {
                padding = box(0.px, 0.px, 15.px, 0.px)
            }

            names {
                padding = box(0.px, 15.px, 0.px, 0.px)
            }
        }

        insertUnit {
            squeezeBox {
                padding = box(15.px, 0.px, 0.px, 0.px)
            }

            unitButtons {
                button {
                    maxWidth = infinity
                    padding = box(6.px)
                    backgroundInsets += box(0.px)
                    backgroundRadius += box(0.px)
                }
            }

            padding = box(10.px)
            borderInsets += box(10.px)
            borderColor += box(Color.GRAY)
        }

        insertUnits {
            backgroundColor += Color.TRANSPARENT
        }

        dragDropField {
            backgroundColor += c(Color.GRAY.toString(), 0.1)
        }

        drawer {
            backgroundImage += javaClass.getResource("/images/airplane-image.png").toURI()!!
            backgroundRepeat += BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT
            backgroundPosition += BackgroundPosition(null, 600.0, false, null, 50.0, false)

            drawerItem {
                backgroundColor += c("#f4f4f4")
            }
        }

        statusBar {
            text {
                fontSize = 14.px
            }
        }
    }
}
