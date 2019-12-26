package worder.database

import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import java.nio.file.Path
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SqlLiteFileTest {
    companion object {
        private val pathToTestDbs = "testDbs"

        @JvmStatic
        @BeforeClass
        fun copyDbFiles() {
//            Path.of(pathToTestDbs).toFile().walk()
//                .filter { it.toString().endsWith(".bck") }
//                .forEach { it.copyTo(File("testDbs/tmp//${it.name}"), true) }
        }

        @JvmStatic
        @AfterClass
        fun removeDbFiles() {
            //File("testDbs/tmp").deleteRecursively()
        }
    }

    @Test
    fun fileNameValidationTest() {
//        assertFalse(SqlLiteFile.isValidSqlLiteDb(Path.of("$pathToTestDbs/novalid1.bckkk")))
//        assertFalse(SqlLiteFile.isValidSqlLiteDb(Path.of("$pathToTestDbs/noValid2.txt")))
//
//        assertTrue(SqlLiteFile.isValidSqlLiteDb(Path.of("$pathToTestDbs/general.bck")))
//        assertTrue(SqlLiteFile.isValidSqlLiteDb(Path.of("$pathToTestDbs/withoutEnglishDic.bck")))
//        assertTrue(SqlLiteFile.isValidSqlLiteDb(Path.of("$pathToTestDbs/withoutTags.bck")))
    }

    @Test
    fun testInitBlock() {
        assertFailsWith<IllegalArgumentException> { SqlLiteFile("testDbs/tmp/withoutEnglishDic.bck") }

        SqlLiteFile("testDbs/tmp/withoutTags.bck")
        SqlLiteFile("testDbs/tmp/general.bck")
    }
}