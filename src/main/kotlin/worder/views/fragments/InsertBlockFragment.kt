package worder.views.fragments

import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.hbox
import worder.views.styles.WorderStyle

class InsertBlockFragment : Fragment() {
//    private val insertBlock: InsertBlock by param()

    override val root = hbox {
        addClass(WorderStyle.insertBlock)

//        hbox {
//            imageview(resources.image("/fragments/document.png"))
//
//            vbox {
//                label("  File name: " + insertBlock.file.name)
//                label("  File size: " + insertBlock.file.length())
//                label("Words total: " + (insertBlock.wordsToInsert.size + insertBlock.wordsToReset.size))
//                label("  Words new: " + insertBlock.wordsToInsert.size)
//                label("  Words old: " + insertBlock.wordsToReset.size)
//            }
//
//            piechart {
//                addClass(WorderStyle.diagram)
//                labelsVisible = false
//                isLegendVisible = false
//
//                data("New", insertBlock.wordsToInsert.size.toDouble())
//                data("Old", insertBlock.wordsToReset.size.toDouble())
//            }
//        }

//        vbox {
//            imageview(resources.image("/fragments/document.png"))
//            addClass(WorderStyle.icon)
//        }
    }
}
