/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <generateFileStamps.sh>
 *
 * Created: <7/2/20, 11:27 PM>
 * Modified: <7/2/20, 11:50 PM>
 * Version: <1>
 */

package worder.database

import worder.database.model.WorderDB

interface DatabaseEventListener {
    fun onDatabaseConnection(db: WorderDB)
    fun onDatabaseDisconnection()
}
