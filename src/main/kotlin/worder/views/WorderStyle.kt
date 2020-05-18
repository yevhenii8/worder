package worder.views

import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment.CENTER
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

        val dragDropField by cssclass()
    }

    init {
        s(text, label) {
            fontSize = 18.px
        }

        statBlock {
            alignment = Pos.CENTER
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

        dragDropField {
            alignment = Pos.CENTER
            backgroundColor += c(Color.GRAY.toString(), 0.1)
        }
    }
}