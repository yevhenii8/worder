package worder.update

import org.junit.Test
import worder.database.LocalWordsUpdateDb
import worder.database.sqllite.SqlLiteFileUpdater

class UpdateModelTest {
    @Test
    fun initTest() {
        val db = SqlLiteFileUpdater("../worder_deprecated/updated.bck")
        val model = UpdateModel(db, LocalWordsUpdateDb.SelectOrder.RANDOM, 0)

        println(model.database.summary)
        println(model.database.worderTrack)
        println(model.database.sessionStat)
        println(model.selectOrder)
        println()
    }

    @Test
    fun learnedCommand() {
        val db = SqlLiteFileUpdater("../worder_deprecated/updated.bck")
        val model = UpdateModel(db, LocalWordsUpdateDb.SelectOrder.RANDOM, 0)

        if(model.hasNext()) {
            val wordBlock = model.next()
            println("definitions: " + wordBlock.definitions)
            println("examples: " + wordBlock.examples)
            println("transcriptions: " + wordBlock.transcriptions)
            println("translations: " + wordBlock.translations)
            println("isCommitted: ${wordBlock.isCommitted}, #${wordBlock.serialNumber}")

            println()

            println(wordBlock.dbWord.name)
            println(wordBlock.dbWord.rate)

            wordBlock.learn()

            println()
            println()

            println("isCommitted: ${wordBlock.isCommitted}, resolution: ${wordBlock.resolution}")
        }
    }

    @Test
    fun shouldNotCommit() {
        val db = SqlLiteFileUpdater("../worder_deprecated/updated.bck")
        val model = UpdateModel(db, LocalWordsUpdateDb.SelectOrder.RANDOM, 1)

        if(model.hasNext()) {
            val wordBlock = model.next()
            println("definitions: " + wordBlock.definitions)
            println("examples: " + wordBlock.examples)
            println("transcriptions: " + wordBlock.transcriptions)
            println("translations: " + wordBlock.translations)
            println("isCommitted: ${wordBlock.isCommitted}, #${wordBlock.serialNumber}")

            println()

            println(wordBlock.dbWord.name)
            println(wordBlock.dbWord.rate)

            wordBlock.learn()

            println()
            println()

            println("isCommitted: ${wordBlock.isCommitted}, resolution: ${wordBlock.resolution}")
        }
    }

    @Test
    fun shouldCommit() {
        val db = SqlLiteFileUpdater("../worder_deprecated/updated.bck")
        val model = UpdateModel(db, LocalWordsUpdateDb.SelectOrder.RANDOM, 1)

        if(model.hasNext()) {
            val wordBlock = model.next()
            println("definitions: " + wordBlock.definitions)
            println("examples: " + wordBlock.examples)
            println("transcriptions: " + wordBlock.transcriptions)
            println("translations: " + wordBlock.translations)
            println("isCommitted: ${wordBlock.isCommitted}, #${wordBlock.serialNumber}")

            println()

            println(wordBlock.dbWord.name)
            println(wordBlock.dbWord.rate)

            wordBlock.learn()

            println()
            println()

            println("Before exit: isCommitted: ${wordBlock.isCommitted}, resolution: ${wordBlock.resolution}")
            model.exit()
            println("After: isCommitted: ${wordBlock.isCommitted}, resolution: ${wordBlock.resolution}")
        }

    }
}
