package worder.model.update

import org.junit.Test
import worder.database.WordsUpdateDB
import worder.database.sqllite.SqlLiteFileUpdater
import worder.model.UpdateModel

class UpdateModelTest {
    @Test
    fun initTest() {
        val db = SqlLiteFileUpdater("../worder_deprecated/updated.bck")
        val model = UpdateModel(db, WordsUpdateDB.SelectOrder.RANDOM)

        println(model.database.summary)
        println(model.database.worderTrack)
        println(model.database.sessionStat)
        println(model.selectOrder)
        println()

        model.requesters.forEach { println(it.sessionStat) }
    }
}
