package worder.core.view

import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.input.TransferMode
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import tornadofx.FileChooserMode
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.chooseFile
import tornadofx.hbox
import tornadofx.hyperlink
import tornadofx.imageview
import tornadofx.label
import tornadofx.vbox
import tornadofx.warning
import worder.core.styles.WorderDefaultStyles
import java.io.File

class DragAndDropFragment : Fragment() {
    private val text: String by param()
    private val windowTitle: String by param()
    private val extensionFilter: FileChooser.ExtensionFilter by param()
    private val action: (files: List<File>) -> Unit by param()
    private val allowMultiselect: Boolean by param()

    override val root: Parent = vbox(alignment = Pos.CENTER) {
        addClass(WorderDefaultStyles.dragDropField)

        imageview("/images/files-image.png") {
            VBox.setMargin(this, Insets(20.0))
        }

        label(text)

        hbox(alignment = Pos.CENTER) {
            label("Or ")
            hyperlink("choose your file${if (allowMultiselect) "(s)" else ""}") {
                setOnAction {
                    val chosenFiles = chooseFile(
                            title = windowTitle,
                            filters = arrayOf(extensionFilter),
                            mode = if (allowMultiselect) FileChooserMode.Multi else FileChooserMode.Single
                    )
                    if (chosenFiles.isNotEmpty())
                        action.invoke(chosenFiles)
                }
            }
        }

        setOnDragOver { dragEvent ->
            if (dragEvent.gestureSource !== this && dragEvent.dragboard.hasFiles())
                dragEvent.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
            dragEvent.consume()
        }

        setOnDragDropped { dragEvent ->
            dragEvent.dragboard.files.let {
                if (!allowMultiselect && it.size != 1)
                    warning("Drag & Drop Selector", "Only one file is needed!")
                else
                    action.invoke(dragEvent.dragboard.files)
            }
            dragEvent.consume()
        }
    }
}