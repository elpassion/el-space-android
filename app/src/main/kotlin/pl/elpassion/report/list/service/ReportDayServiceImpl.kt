package pl.elpassion.report.list.service

import pl.elpassion.common.extensions.*
import pl.elpassion.report.DailyReport
import pl.elpassion.report.HoursReport
import pl.elpassion.report.Report
import pl.elpassion.report.list.*
import rx.Observable

class ReportDayServiceImpl(private val reportListService: ReportList.Service) : ReportDayService {

    override fun createDays(dateChangeObservable: Observable<YearMonth>): Observable<List<Day>> =
            Observable.combineLatest(dateChangeObservable,
                    reportListService.getReports(), { t1, t2 -> Pair(t1, t2) })
                    .map { createDaysWithReports(it.first, it.second) }

    private fun createDaysWithReports(yearMonth: YearMonth, reportList: List<Report>) =
            (1..yearMonth.month.daysInMonth).map { dayNumber ->
                val calendarForDay = getCalendarForDay(yearMonth, dayNumber)
                val reports = reportList.filter(isFromSelectedDay(yearMonth, dayNumber))
                if (reports.isEmpty()) {
                    DayWithoutReports(uuid = createDayUUid(yearMonth.year, yearMonth.month.index, dayNumber),
                            name = "$dayNumber ${calendarForDay.dayName()}",
                            hasPassed = calendarForDay.isNotAfter(getCurrentTimeCalendar()),
                            date = getPerformedAtString(yearMonth.year, yearMonth.month.index + 1, dayNumber),
                            isWeekend = calendarForDay.isWeekendDay())

                } else if (reports.all { it is HoursReport }) {
                    DayWithHourlyReports(uuid = createDayUUid(yearMonth.year, yearMonth.month.index, dayNumber),
                            reports = reports.filterIsInstance<HoursReport>(),
                            hasPassed = calendarForDay.isNotAfter(getCurrentTimeCalendar()),
                            name = "$dayNumber ${calendarForDay.dayName()}",
                            date = getPerformedAtString(yearMonth.year, yearMonth.month.index + 1, dayNumber))

                } else if (reports.all { it is DailyReport } && reports.size == 1) {
                    DayWithDailyReport(uuid = createDayUUid(yearMonth.year, yearMonth.month.index, dayNumber),
                            report = reports.filterIsInstance<DailyReport>().first(),
                            hasPassed = calendarForDay.isNotAfter(getCurrentTimeCalendar()),
                            name = "$dayNumber ${calendarForDay.dayName()}",
                            date = getPerformedAtString(yearMonth.year, yearMonth.month.index + 1, dayNumber))
                } else {
                    throw IllegalArgumentException()
                }
            }

    private fun isFromSelectedDay(yearMonth: YearMonth, day: Int): (Report) -> Boolean = { report ->
        report.year == yearMonth.year && report.month == yearMonth.month.index + 1 && report.day == day
    }

    private fun getCalendarForDay(yearMonth: YearMonth, dayNumber: Int) = getTimeFrom(year = yearMonth.year, month = yearMonth.month.index, day = dayNumber)

}