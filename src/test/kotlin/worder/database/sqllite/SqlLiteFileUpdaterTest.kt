package worder.database.sqllite

import org.junit.Test
import worder.database.WordsInsertDB
import worder.database.WordsUpdateDB
import worder.model.BaseWord
import worder.model.Word

class SqlLiteFileUpdaterTest {
    private val pathToSampleDb = "/home/yevhenii/IdeaProjects/worder_deprecated/updated.bck"
    private val db: WordsUpdateDB = SqlLiteFileUpdater(pathToSampleDb)

    @Test
    fun setLearned() {
        db.setLearned(BaseWord("hell", null))
        db.setLearned(BaseWord("hello", null))
        db.setLearned(BaseWord("table", null))
    }

    @Test
    fun removeWord() {
        db.removeWord(BaseWord("hell", null))
        db.removeWord(BaseWord("hello", null))
        db.removeWord(BaseWord("table", null))
    }
}
