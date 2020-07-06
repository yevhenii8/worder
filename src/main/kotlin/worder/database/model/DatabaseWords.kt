/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <DatabaseWords.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <06/07/2020, 07:25:08 PM>
 * Version: <3>
 */

package worder.database.model

import worder.core.model.Word

class DatabaseWord(
        name: String,
        transcription: String?,

        val rate: Int,
        val register: Long,
        val lastModification: Long,
        val lastRateModification: Long,
        val lastTraining: Int,

        val translations: Set<String>,
        val examples: Set<String>
) : Word(name, transcription)

class UpdatedWord(
        name: String,
        transcription: String?,

        val primaryDefinition: String,
        val secondaryDefinition: String?,
        val examples: Set<String>
) : Word(name, transcription)
