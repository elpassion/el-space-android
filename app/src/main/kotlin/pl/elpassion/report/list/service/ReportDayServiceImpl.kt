package pl.elpassion.report.list.service

import pl.elpassion.common.extensions.*
import pl.elpassion.report.HoursReport
import pl.elpassion.report.list.RegularDay
import pl.elpassion.report.list.ReportList
import pl.elpassion.report.list.YearMonth
import pl.elpassion.report.list.createDayUUid
import rx.Observable

class ReportDayServiceImpl(private val reportListService: ReportList.Service) : ReportDayService {

    override fun createDays(dateChangeObservable: Observable<YearMonth>): Observable<List<RegularDay>> =
            Observable.combineLatest(dateChangeObservable,
                    reportListService.getReports(), { t1, t2 -> Pair(t1, t2) })
                    .map { createDaysWithReports(it.first, it.second.filterIsInstance<HoursReport>()) }

    private fun createDaysWithReports(yearMonth: YearMonth, reportList: List<HoursReport>) =
            (1..yearMonth.month.daysInMonth).map { dayNumber ->
                val calendarForDay = getCalendarForDay(yearMonth, dayNumber)
                RegularDay(reports = reportList.filter(isFromSelectedDay(yearMonth, dayNumber)),
                        hasPassed = calendarForDay.isNotAfter(getCurrentTimeCalendar()),
                        isWeekendDay = calendarForDay.isWeekendDay(),
                        name = "$dayNumber ${calendarForDay.dayName()}",
                        date = getPerformedAtString(yearMonth.year, yearMonth.month.index + 1, dayNumber),
                        uuid = createDayUUid(yearMonth.year, yearMonth.month.index, dayNumber))
            }

    private fun isFromSelectedDay(yearMonth: YearMonth, day: Int): (HoursReport) -> Boolean = { report ->
        report.year == yearMonth.year && report.month == yearMonth.month.index + 1 && report.day == day
    }

    private fun getCalendarForDay(yearMonth: YearMonth, dayNumber: Int) = getTimeFrom(year = yearMonth.year, month = yearMonth.month.index, day = dayNumber)

}