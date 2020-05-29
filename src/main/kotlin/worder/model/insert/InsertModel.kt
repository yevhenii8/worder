package worder.model.insert

import java.io.File

interface InsertModel {
    fun prepareBatch(files: List<File>): InsertBatch
}
