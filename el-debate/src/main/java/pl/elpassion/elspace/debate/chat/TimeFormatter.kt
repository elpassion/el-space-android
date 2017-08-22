package pl.elpassion.elspace.debate.chat

import java.sql.Date
import java.util.*

class TimeFormatter(private val timeStamp: Long, private val timeZone: TimeZone) {

    fun getTime(): String {
        val date = Date(timeStamp * 1000)
        val calendar = Calendar.getInstance().apply {
            timeZone = this@TimeFormatter.timeZone
            time = date
        }
        return Formatter().format("%tR", calendar).toString()
    }
}