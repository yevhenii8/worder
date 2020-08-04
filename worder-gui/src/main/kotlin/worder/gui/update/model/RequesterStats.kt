/**
 * Stamp was generated by <generateFileStamps.sh>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <RequesterStats.kt>
 * Created: <02/07/2020, 11:27:00 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <5>
 */

package worder.gui.update.model

import worder.gui.core.model.ObservableStats

interface ObservableRequesterStats : ObservableStats {
    val totalRequests: Int


    // they are nullable by design
    // since we don't know which interfaces concrete requester implements

    val totalDefinitions: Int?
    val totalTranslations: Int?
    val totalExamples: Int?
    val totalTranscriptions: Int?
}