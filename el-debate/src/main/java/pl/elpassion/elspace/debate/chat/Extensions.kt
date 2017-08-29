package pl.elpassion.elspace.debate.chat

import pl.elpassion.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

fun Long.formatMillisToTime(myTimeZone: TimeZone = TimeZone.getDefault()): String = SimpleDateFormat("HH:mm", Locale.US).let {
    it.timeZone = myTimeZone
    it.format(Date(this))
}

fun String.printIfDebug() {
    if (BuildConfig.DEBUG) println(this)
}