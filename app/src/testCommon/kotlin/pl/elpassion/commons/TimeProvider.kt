package pl.elpassion.commons

import pl.elpassion.common.CurrentTimeProvider
import pl.elpassion.common.extensions.toCalendarDate
import java.util.*


fun stubCurrentTime(year: Int = 2016, month: Int = 6, day: Int = 1) {
    stubCurrentTime(Calendar.getInstance().apply { set(year, month - 1, day, 12, 0) })
}

fun stubCurrentTime(date: String) {
    stubCurrentTime(date.toCalendarDate())
}

private fun stubCurrentTime(calendarDate: Calendar) {
    CurrentTimeProvider.override = { calendarDate.timeInMillis }
}