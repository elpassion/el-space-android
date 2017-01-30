package pl.elpassion.elspace.commons

import pl.elpassion.elspace.common.CurrentTimeProvider
import pl.elpassion.elspace.common.extensions.toCalendarDate
import java.util.*

fun stubCurrentTime(year: Int = 2016, month: Int = 6, day: Int = 1) {
    CurrentTimeProvider.override = {
        Calendar.getInstance().apply { set(year, month - 1, day, 12, 0) }.timeInMillis
    }
}

fun stubCurrentTime(date: String) {
    CurrentTimeProvider.override = {
        date.toCalendarDate().timeInMillis
    }
}