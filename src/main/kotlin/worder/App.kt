package worder

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.input.DragEvent
import javafx.scene.input.TransferMode
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import tornadofx.App
import worder.views.MainView
import java.awt.desktop.AppEvent


class AppEntry : App(MainView::class)

//class AppEntry : Application() {
//    companion object {
//        @JvmStatic fun main(args: Array<String>) {
//            launch(AppEntry::class.java, *args)
//        }
//    }
//
//    override fun start(primaryStage: Stage?) {
//        val label = Label("Drag a file to me.")
//        val dropped = Label("")
//        val dragTarget = VBox()
//        dragTarget.children.addAll(label, dropped)
//        dragTarget.setOnDragOver { event ->
//            if (event.gestureSource !== dragTarget
//                    && event.dragboard.hasFiles()) {
//                /* allow for both copying and moving, whatever user chooses */
//                event.acceptTransferModes(*TransferMode.COPY_OR_MOVE)
//            }
//            event.consume()
//        }
//
//        dragTarget.setOnDragDropped { event ->
//            val db = event.dragboard
//            var success = false
//            if (db.hasFiles()) {
//                dropped.setText(db.files.toString())
//                success = true
//            }
//            /* let the source know whether the string was successfully
//                 * transferred and used */event.isDropCompleted = success
//            event.consume()
//        }
//
//
//        val root = StackPane()
//        root.children.add(dragTarget)
//
//        val scene = Scene(root, 500.0, 250.0)
//
//        primaryStage!!.title = "Drag Test"
//        primaryStage.scene = scene
//        primaryStage.show()
//    }
//}
