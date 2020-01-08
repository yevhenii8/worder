package worder.database.sqllite

import org.junit.Test
import worder.database.WordsExtractDb
import worder.BaseWord

class SqlLiteFileInserterTest {
    private val pathToSampleDb = "/home/yevhenii/IdeaProjects/worder_deprecated/updated.bck"
    private val db: WordsExtractDb = SqlLiteFileInserter(pathToSampleDb)

    @Test
    fun baseTest() {
//        assertFails { SqlLiteFileInserter("") }
//        assertFails { SqlLiteFileInserter("hell") }
//        assertFails { SqlLiteFileInserter("hell.bck") }
//
//        assertTrue(db.containsWord(Word("hell", null)))
//        assertTrue(db.containsWord(UpdatedWord("hell", null, "", null)))
//        assertTrue(db.containsWord(DatabaseWord("hell", null, 0, 0, 0, 0, 0)))
//
//        assertFalse(db.containsWord(Word("has annotation", null)))
//        assertFalse(db.containsWord(UpdatedWord("has annotation", null, "", null)))
//        assertFalse(db.containsWord(DatabaseWord("has annotation", null, 0, 0, 0, 0, 0)))
    }

    @Test
    fun insertWordTest() {
//        db.insertWord(Word("testInsert1", null))
//        db.insertWord(UpdatedWord("testInsert2", null, "", null))
//        db.insertWord(DatabaseWord("testInsert3", null, 0, 0, 0,0,0))
    }

    @Test
    fun resetWordTest() {
        db.resetWord(BaseWord("hell", null))
        db.resetWord(BaseWord("hello", null))
        db.resetWord(BaseWord("table", null))
    }
}