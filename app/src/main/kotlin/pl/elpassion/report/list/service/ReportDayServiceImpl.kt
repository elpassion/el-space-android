package pl.elpassion.report.list.service

import pl.elpassion.common.extensions.*
import pl.elpassion.report.DailyReport
import pl.elpassion.report.HourlyReport
import pl.elpassion.report.Report
import pl.elpassion.report.list.*
import rx.Observable
import java.util.*

class ReportDayServiceImpl(private val reportListService: ReportList.Service) : ReportDayService {

    override fun createDays(dateChangeObservable: Observable<YearMonth>): Observable<List<Day>> =
            Observable.combineLatest(dateChangeObservable,
                    reportListService.getReports(), { t1, t2 -> Pair(t1, t2) })
                    .map { createDaysWithReports(it.first, it.second) }

    private fun createDaysWithReports(yearMonth: YearMonth, reportList: List<Report>) =
            (1..yearMonth.month.daysInMonth).map { dayNumber ->
                val calendarForDay = getCalendarForDay(yearMonth, dayNumber)
                val reports = reportList.filter(isFromSelectedDay(yearMonth, dayNumber))
                createDayWithReports(calendarForDay, dayNumber, reports, yearMonth)
            }

    private fun createDayWithReports(calendarForDay: Calendar, dayNumber: Int, reports: List<Report>, yearMonth: YearMonth): Day {
        val hasPassed = calendarForDay.isNotAfter(getCurrentTimeCalendar())
        val date = getPerformedAtString(yearMonth.year, yearMonth.month.index + 1, dayNumber)
        val dayName = "$dayNumber ${calendarForDay.dayName()}"
        val uuid = createDayUUid(yearMonth.year, yearMonth.month.index, dayNumber)
        return when {
            reports.isEmpty() -> createDayWithoutReports(uuid, calendarForDay, hasPassed, date, dayName)
            reports.all { it is HourlyReport } -> createDayWithHourlyReports(uuid, reports, hasPassed, date, dayName)
            reports.all { it is DailyReport } && reports.size == 1 -> createDayWithDailyReports(uuid, reports, hasPassed, date, dayName)
            else -> throw IllegalArgumentException()
        }
    }

    private fun createDayWithDailyReports(uuid: Long, reports: List<Report>, hasPassed: Boolean, date: String, dayName: String): DayWithDailyReport {
        return DayWithDailyReport(uuid= uuid,
                report = reports.filterIsInstance<DailyReport>().first(),
                hasPassed = hasPassed,
                name = dayName,
                date = date)
    }

    private fun createDayWithHourlyReports(uuid: Long, reports: List<Report>, hasPassed: Boolean, date: String, dayName: String): DayWithHourlyReports {
        return DayWithHourlyReports(uuid= uuid,
                reports = reports.filterIsInstance<HourlyReport>(),
                hasPassed = hasPassed,
                name = dayName,
                date = date)
    }

    private fun createDayWithoutReports(uuid: Long, calendarForDay: Calendar, hasPassed: Boolean, date: String, dayName: String): DayWithoutReports {
        return DayWithoutReports(uuid= uuid,
                name = dayName,
                hasPassed = hasPassed,
                date = date,
                isWeekend = calendarForDay.isWeekendDay())
    }

    private fun isFromSelectedDay(yearMonth: YearMonth, day: Int): (Report) -> Boolean = { report ->
        report.year == yearMonth.year && report.month == yearMonth.month.index + 1 && report.day == day
    }

    private fun getCalendarForDay(yearMonth: YearMonth, dayNumber: Int) = getTimeFrom(year = yearMonth.year, month = yearMonth.month.index, day = dayNumber)

}