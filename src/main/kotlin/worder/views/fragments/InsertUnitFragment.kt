package worder.views.fragments

import javafx.geometry.Pos
import javafx.scene.layout.HBox
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.insets
import tornadofx.label
import tornadofx.vbox
import worder.model.insert.InsertUnit
import worder.views.styles.WorderStyle

class InsertUnitFragment : Fragment() {
    private val insertUnit: InsertUnit by param()
    private val unitStats = insertUnit.unitStats

    override val root = hbox {
        imageview("/icons/blue-document-icon_64x64.png") {
            HBox.setMargin(this, insets(right = 15))
            alignment = Pos.CENTER
        }

        vbox {
            label("File name: ${unitStats.fileName}")
            label("File size: ${unitStats.fileSize}")
            label("")
            label("Unit status: ${unitStats.status}")
            label("Unit id: ${unitStats.id}")
            label("")
            label("Valid words: ${unitStats.validWords}")
            label("Invalid words: ${unitStats.invalidWords}")
        }

        addClass(WorderStyle.insertUnit)
    }
}
