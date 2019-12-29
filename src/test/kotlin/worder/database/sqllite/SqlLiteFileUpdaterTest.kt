package worder.database.sqllite

import org.junit.Test
import worder.database.WordsInsertDB
import worder.database.WordsUpdateDB
import worder.model.Word

class SqlLiteFileUpdaterTest {
    private val pathToSampleDb = "/home/yevhenii/IdeaProjects/worder_deprecated/updated.bck"
    private val db: WordsUpdateDB = SqlLiteFileUpdater(pathToSampleDb)

    @Test
    fun setLearned() {
        db.setLearned(Word("hell", null))
        db.setLearned(Word("hello", null))
        db.setLearned(Word("table", null))
    }

    @Test
    fun removeWord() {
        db.removeWord(Word("hell", null))
        db.removeWord(Word("hello", null))
        db.removeWord(Word("table", null))
    }
}
