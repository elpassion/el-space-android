package pl.elpassion.report.list

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.elpassion.common.extensions.daysForCurrentMonth
import pl.elpassion.common.extensions.getFullMonthName
import pl.elpassion.common.extensions.getTimeFrom

class CalendarToYearMonthConverterTest {
    @Test
    fun shouldConvertProperYear() {
        val calendar = getTimeFrom(2016, 1, 1)

        val yearMonth = calendar.toYearMonth()

        assertEquals(yearMonth.year, 2016)
    }

    @Test
    fun shouldConvertProperMonth() {
        val calendar = getTimeFrom(2016, 11, 1)

        val yearMonth = calendar.toYearMonth()

        assertEquals(yearMonth.month.index, 11)
    }

    @Test
    fun shouldConvertProperMonthName() {
        val calendar = getTimeFrom(2016, 11, 21)

        val yearMonth = calendar.toYearMonth()

        assertEquals(yearMonth.month.monthName, calendar.getFullMonthName())
    }

    @Test
    fun shouldConvertProperMonthNames() {
        val calendar = getTimeFrom(2016, 11, 21)

        val yearMonth = calendar.toYearMonth()

        assertEquals(yearMonth.month.daysInMonth, calendar.daysForCurrentMonth())
    }

}