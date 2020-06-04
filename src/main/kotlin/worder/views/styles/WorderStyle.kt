package worder.views.styles

import javafx.geometry.Pos
import javafx.scene.layout.BackgroundPosition
import javafx.scene.layout.BackgroundRepeat
import javafx.scene.layout.BackgroundSize
import javafx.scene.paint.Color
import tornadofx.DrawerStyles.Companion.drawer
import tornadofx.DrawerStyles.Companion.drawerItem
import tornadofx.MultiValue
import tornadofx.Stylesheet
import tornadofx.box
import tornadofx.c
import tornadofx.cssclass
import tornadofx.px

class WorderStyle : Stylesheet() {
    companion object {
        val statBlock by cssclass()
        val title by cssclass()
        val names by cssclass()

        val insertBlock by cssclass()
        val icon by cssclass()
        val diagram by cssclass()

        val dragDropField by cssclass()
        val dashboard by cssclass()
        val statusBar by cssclass()
    }

    init {
        s(text, label) {
            fontSize = 18.px
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

        insertBlock {
            padding = box(15.px)
            borderInsets += box(15.px)
            borderColor += box(Color.GRAY)

            icon {
                alignment = Pos.CENTER
                padding = box(0.px, 10.px, 0.px, 0.px)
            }

            title {
                padding = box(0.px, 0.px, 10.px, 0.px)
            }

            diagram {
                padding = box((-10).px)
                labelPadding = box(0.px)

                borderImageInsets += box(0.px)
                borderInsets += box(0.px)
                backgroundInsets += box(0.px)

                maxWidth = 25.px
                maxHeight = 25.px
                labelLineLength = 0.px
                borderColor += box(Color.GREEN)
            }
        }

        dragDropField {
            alignment = Pos.CENTER
            backgroundColor += c(Color.GRAY.toString(), 0.1)
        }

        dashboard {
            alignment = Pos.TOP_CENTER
        }

        drawer {
            backgroundImage += javaClass.getResource("/airplane-image.png").toURI()!!
            backgroundRepeat += BackgroundRepeat.NO_REPEAT to BackgroundRepeat.NO_REPEAT
            backgroundPosition += BackgroundPosition(null, 600.0, false, null, 50.0, false)

            drawerItem {
                backgroundColor += c("#f4f4f4")
            }
        }

        statusBar {
            text {
                fontSize = 15.px
            }
        }
    }
}
