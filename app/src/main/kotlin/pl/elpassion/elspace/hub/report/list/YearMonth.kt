package pl.elpassion.elspace.hub.report.list

import pl.elpassion.elspace.common.extensions.daysForCurrentMonth
import pl.elpassion.elspace.common.extensions.getFullMonthName
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