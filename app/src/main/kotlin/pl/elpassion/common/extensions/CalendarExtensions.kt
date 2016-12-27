package pl.elpassion.common.extensions

import pl.elpassion.common.CurrentTimeProvider
import java.text.SimpleDateFormat
import java.util.*

fun Calendar.changeToPreviousMonth() = add(Calendar.MONTH, -1)
fun Calendar.changeToNextMonth() = add(Calendar.MONTH, 1)
fun Calendar.isNotAfter(sth: Any) = !after(sth)
fun getCurrentTimeCalendar(): Calendar = Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }
fun getTimeFrom(year: Int, month: Int, day: Int): Calendar = Calendar.getInstance().apply { set(year, month, day, 12, 0) }
fun getTimeFrom(timeInMillis: Long): Calendar = Calendar.getInstance().apply { setTimeInMillis(timeInMillis) }
fun Calendar.getFullMonthName(): String = SimpleDateFormat("MMMM", Locale.UK).format(this.time)
fun Calendar.isWeekendDay(): Boolean = get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
fun Calendar.dayName(): String = SimpleDateFormat("EEE", Locale.UK).run { format(this@dayName.time) }
fun getPerformedAtString(year: Int, month: Int, day: Int) = String.format("%d-%02d-%02d", year, month, day)
fun Calendar.daysForCurrentMonth() = getActualMaximum(Calendar.DAY_OF_MONTH)
fun String.toCalendarDate(): Calendar {
    val date = SimpleDateFormat("yyyy-MM-dd").parse(this)
    val calendar = Calendar.getInstance().apply { time = date }
    return calendar
}
val Calendar.dayOfWeek: Int
    get() = this.get(java.util.Calendar.DAY_OF_WEEK)
val Calendar.month: Int
    get() = this.get(java.util.Calendar.MONTH)
val Calendar.year: Int
    get() = this.get(java.util.Calendar.YEAR)