package pl.elpassion.elspace.common.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Long.formatMillisToTime(myTimeZone: () -> TimeZone): String = SimpleDateFormat("HH:mm", Locale.US).let {
    it.timeZone = myTimeZone()
    it.format(Date(this))
}
