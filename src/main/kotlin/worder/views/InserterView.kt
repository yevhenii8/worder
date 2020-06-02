package worder.views

import tornadofx.View
import tornadofx.fitToHeight
import tornadofx.hbox
import tornadofx.vbox
import worder.controllers.InserterController
import worder.views.fragments.InsertBlockFragment
import java.io.File

class InserterView : View("Inserter") {
//    private val insertBlock = object : InsertBlock {
//        override val file: File = File("/home/yevhenii/Other/inserter/words1.txt")
//        override val isCommitted: Boolean = false
//        override val wordsToReset: Set<String> = setOf("heaven")
//        override val wordsToInsert: Set<String> = setOf("hell", "hell1", "hell2", "hell3", "hell4", "hell5")
//
//        override fun commit() {}
//    }

    override val root = vbox {
        isFillWidth = false
//        add(find<InsertBlockFragment>("insertBlock" to insertBlock))
//        add(find<InsertBlockFragment>("insertBlock" to insertBlock))
    }
}
