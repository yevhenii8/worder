package worder.database.sqllite

import worder.database.InserterSessionStat
import worder.database.WordsInsertDB
import worder.model.BaseWord

class SqlLiteFileInserter(fileName: String) : SqlLiteFile(fileName), WordsInsertDB {
    override val sessionStat: InserterSessionStat
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun resolveWord(word: BaseWord) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
