package worder.update.view

import javafx.beans.property.SimpleMapProperty
import javafx.scene.Parent
import tornadofx.View
import tornadofx.asObservable
import tornadofx.button
import tornadofx.getValue
import tornadofx.hbox
import tornadofx.label
import tornadofx.paddingLeft
import tornadofx.paddingTop
import tornadofx.replaceChildren
import tornadofx.stackpane
import tornadofx.vbox
import worder.database.DatabaseController
import worder.database.DatabaseEventListener
import worder.database.model.WorderDB
import worder.tornadofx.bindChildren
import worder.database.view.NoConnectionFragment

class UpdateView : View("Update"), DatabaseEventListener {
    private val noConnectionFragment = find<NoConnectionFragment>().root


    init {
        find<DatabaseController>().subscribe(this)
    }


    override val root: Parent = stackpane {
        add(noConnectionFragment)
    }


    override fun onDatabaseConnection(db: WorderDB) {
        root.replaceChildren(find<UpdateConnectedView>().root)
    }

    override fun onDatabaseDisconnection() {
        root.replaceChildren(noConnectionFragment)
    }
}

class UpdateConnectedView : View() {
    /*
    Referenced with https://github.com/edvin/tornadofx2/issues/8
     */

    private val backedMap = LinkedHashMap<String, Int>().asObservable()
    private val mapProperty: SimpleMapProperty<String, Int> = SimpleMapProperty(backedMap)
    private val map: MutableMap<String, Int> by mapProperty

    override val root: Parent = vbox(spacing = 10) {
        paddingLeft = 15

        map["number1"] = 5
        map["number2"] = 10
        map["number3"] = 15

        vbox(spacing = 10) {
            bindChildren(mapProperty) { key, value ->
                label("$key: $value")
            }
        }

        hbox(spacing = 10) {
            button("increment number2").setOnAction {
                map["number2"] = map["number2"]?.plus(1)!!
            }
            button("decrement number2").setOnAction {
                map["number2"] = map["number2"]?.minus(1)!!
            }
        }

        vbox(spacing = 10) {
            paddingTop = 25

            val button = button("show map")

            button.setOnAction {
                children.removeIf { it != button }
                for ((title, value) in mapProperty)
                    label("$title: $value")
            }
        }
    }
}