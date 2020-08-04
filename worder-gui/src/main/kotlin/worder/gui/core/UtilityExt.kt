/**
 * Stamp was generated by <StampedFile.kt>
 * Last time was modified by <StampedFile.kt>
 *
 * Name: <UtilityExt.kt>
 * Created: <17/07/2020, 09:32:24 PM>
 * Modified: <04/08/2020, 07:11:08 PM>
 * Version: <14>
 */

package worder.gui.core

import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.TemporalAccessor
import kotlin.math.round

val defaultFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withZone(ZoneId.systemDefault())

fun TemporalAccessor.formatted(): String = defaultFormatter.format(this)

fun Number.formatGrouped(): String {
    val num = this.toString()
    val len = num.length

    if (len < 4)
        return num

    val res = StringBuilder(len + len / 3)
    res.append(num.substring(0, len % 3))

    for (i in (len % 3) until len step 3) {
        res.append(" " + num.substring(i, i + 3))
    }

    return res.toString()
}

fun Double.round(decimals: Int): Double {
    var multiplier = 1.0
    repeat(decimals) { multiplier *= 10 }
    return round(this * multiplier) / multiplier
}

fun log(msg: String) = println("[${LocalTime.now()}] [${Thread.currentThread().name}] $msg")

val <T> Set<T>.lastIndex: Int
    get() = this.size - 1