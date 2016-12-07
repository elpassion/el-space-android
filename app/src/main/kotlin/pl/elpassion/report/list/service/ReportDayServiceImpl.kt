package pl.elpassion.report.list.service

import pl.elpassion.common.extensions.*
import pl.elpassion.report.Report
import pl.elpassion.report.list.Day
import pl.elpassion.report.list.ReportList
import pl.elpassion.report.list.YearMonth
import rx.Observable

class ReportDayServiceImpl(private val observeDateChange: DateChangeObserver,
                           private val reportListApi: ReportList.Service) : ReportDayService {
    override fun changeMonthToNext() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun changeMonthToPrevious() {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createDays(): Observable<List<Day>> =
            Observable.combineLatest(observeDateChange.observe(),
                    reportListApi.getReports(), { t1, t2 -> Pair(t1, t2) })
                    .map { createDaysWithReports(it.first, it.second) }

    private fun createDaysWithReports(yearMonth: YearMonth, reportList: List<Report>): List<Day> {
        val days = (1..yearMonth.month.daysInMonth).map { dayNumber ->
            val calendarForDay = getCalendarForDay(yearMonth, dayNumber)
            Day(reports = reportList.filter(isFromSelectedDay(yearMonth, dayNumber)),
                    hasPassed = calendarForDay.isNotAfter(getCurrentTimeCalendar()),
                    isWeekendDay = calendarForDay.isWeekendDay(),
                    name = "$dayNumber ${calendarForDay.dayName()}",
                    date = getPerformedAtString(yearMonth.year, yearMonth.month.index + 1, dayNumber))
        }

        return days
    }

    private fun isFromSelectedDay(yearMonth: YearMonth, day: Int): (Report) -> Boolean = { report ->
        report.year == yearMonth.year && report.month == yearMonth.month.index + 1 && report.day == day
    }

    private fun getCalendarForDay(yearMonth: YearMonth, dayNumber: Int) = getTimeFrom(year = yearMonth.year, month = yearMonth.month.index, day = dayNumber)

    override fun observeDateChanges() = observeDateChange.observe()
}