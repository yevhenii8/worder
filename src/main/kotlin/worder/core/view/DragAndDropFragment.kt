package worder.core.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.input.TransferMode
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.chooseFile
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.label
import tornadofx.vbox
import java.io.File

class DragAndDropFragment : Fragment() {
    private val text: String by param()
    private val windowTitle: String by param()
    private val extensionFilter: FileChooser.ExtensionFilter by param()
    private val action: (files: List<File>) -> Unit by param()

    override val root: Parent = vbox(alignment = Pos.CENTER) {
        imageview("/images/files-image.png") {
            VBox.setMargin(this, Insets(20.0))
        }

        label(text)

        hbox(alignment = Pos.CENTER) {
            label("Or ")
            hyperlink("choose your file(s)") {
                setOnAction {
                    action.invoke(chooseFile(windowTitle, arrayOf(extensionFilter)))
                }
            }
        }

        setOnDragOver {
            if (it.gestureSource !== this && it.dragboard.hasFiles())
                it.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
            it.consume()
        }

        setOnDragDropped {
            if (it.dragboard.hasFiles())
                action.invoke(it.dragboard.files)
            it.consume()
        }

        addClass(WorderBrightStyles.dragDropField)
    }
}
