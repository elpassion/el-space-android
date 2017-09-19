package pl.elpassion.elspace.hub.report.list

import pl.elpassion.elspace.common.extensions.daysForCurrentMonth
import pl.elpassion.elspace.common.extensions.getDateString
import pl.elpassion.elspace.common.extensions.getFullMonthName
import pl.elpassion.elspace.common.extensions.getTimeFrom
import java.util.*

data class YearMonth(val year: Int, val month: Month)

data class Month(val index: Int,
                 val monthName: String,
                 val daysInMonth: Int)

fun Calendar.toYearMonth() =
        YearMonth(year = get(Calendar.YEAR),
                month = Month(
                        index = get(Calendar.MONTH),
                        daysInMonth = daysForCurrentMonth(),
                        monthName = getFullMonthName()))

fun YearMonth.toCalendar() = getTimeFrom(year = year, month = month.index , day = 1)

fun YearMonth.toMonthDateRange(): Pair<String, String> {
    val calendar = getTimeFrom(year, month.index, 1)
    return calendar.getDateString() to getTimeFrom(year, month.index, calendar.daysForCurrentMonth()).getDateString()
}
