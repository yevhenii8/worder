/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DatabaseEventListener.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <4>
 */

package worder.gui.database

import worder.gui.database.model.WorderDB

interface DatabaseEventListener {
    fun onDatabaseConnection(db: WorderDB)
    fun onDatabaseDisconnection()
}
