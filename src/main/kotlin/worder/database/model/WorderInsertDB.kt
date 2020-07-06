/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <WorderInsertDB.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <06/07/2020, 07:25:08 PM>
 * Version: <3>
 */

package worder.database.model

import worder.core.model.BareWord

interface WorderInsertDB {
    val observableInserterStats: ObservableInserterStats

    suspend fun resolveWords(bareWords: Collection<BareWord>): Map<BareWord, ResolveRes>

    enum class ResolveRes {
        INSERTED, RESET
    }
}
