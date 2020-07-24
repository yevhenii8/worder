/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DatabaseWords.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <24/07/2020, 10:32:48 PM>
 * Version: <5>
 */

package worder.database.model

import worder.core.model.Word

class DatabaseWord(
        name: String,
        transcription: String?,

        val rate: Int,
        val registered: Long,
        val lastModified: Long,
        val lastRateModified: Long,
        val lastTrained: Long,

        val translations: List<String>,
        val examples: List<String>
) : Word(name, transcription)

class UpdatedWord(
        name: String,

        override val transcription: String,

        val primaryDefinition: String,
        val secondaryDefinition: String?,
        val examples: List<String>
) : Word(name, transcription)
