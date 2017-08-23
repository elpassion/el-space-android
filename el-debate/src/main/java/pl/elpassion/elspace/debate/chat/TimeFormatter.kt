package pl.elpassion.elspace.debate.chat

import java.util.*

fun Long.getTime(myTimeZone: TimeZone = TimeZone.getDefault()): String {
    val calendar = Calendar.getInstance().apply {
        timeZone = myTimeZone
        timeInMillis = this@getTime * 1000
    }
    return Formatter().format("%tR", calendar).toString()
}