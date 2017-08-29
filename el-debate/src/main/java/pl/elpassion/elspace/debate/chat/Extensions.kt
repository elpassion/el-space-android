package pl.elpassion.elspace.debate.chat

import android.util.Log
import pl.elpassion.BuildConfig
import java.text.SimpleDateFormat
import java.util.*

fun Long.formatMillisToTime(myTimeZone: TimeZone = TimeZone.getDefault()): String = SimpleDateFormat("HH:mm", Locale.US).let {
    it.timeZone = myTimeZone
    it.format(Date(this))
}

fun logExceptionIfDebug(source: String, exception: Throwable) {
    if (BuildConfig.DEBUG) Log.e("Exception has occurred:", source, exception)
}

fun logMessageIfDebug(source: String, message: String) {
    if (BuildConfig.DEBUG) Log.i(source, message)
}