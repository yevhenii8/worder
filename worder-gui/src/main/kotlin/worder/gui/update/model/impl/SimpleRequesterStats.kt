/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <SimpleRequesterStats.kt>
 * Created: <20/07/2020, 11:22:35 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <4>
 */

package worder.gui.update.model.impl

import worder.gui.core.model.BaseObservableStats
import worder.gui.update.model.ObservableRequesterStats

open class SimpleRequesterStats(
        origin: String,

        totalRequests: Int = 0,
        totalDefinitions: Int? = null,
        totalTranslations: Int? = null,
        totalExamples: Int? = null,
        totalTranscriptions: Int? = null
) : BaseObservableStats(origin = origin), ObservableRequesterStats {
    override var totalRequests: Int by bindThroughValue(initValue = totalRequests, title = "Total requests")
    override var totalDefinitions: Int? by bindThroughValue(initValue = totalDefinitions, title = "Total definitions")
    override var totalTranslations: Int? by bindThroughValue(initValue = totalTranslations, title = "Total translations")
    override var totalExamples: Int? by bindThroughValue(initValue = totalExamples, title = "Total examples")
    override var totalTranscriptions: Int? by bindThroughValue(initValue = totalTranscriptions, title = "Total transcriptions")
}
