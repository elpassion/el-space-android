package pl.elpassion.elspace.debate.chat

import java.sql.Date
import java.util.*

fun Long.getTime(myTimeZone: TimeZone = TimeZone.getDefault()): String {
    val date = Date(this * 1000)
    val calendar = Calendar.getInstance().apply {
        timeZone = myTimeZone
        time = date
    }
    return Formatter().format("%tR", calendar).toString()
}