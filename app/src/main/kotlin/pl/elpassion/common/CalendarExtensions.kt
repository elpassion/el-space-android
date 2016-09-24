package pl.elpassion.common

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.changeToPreviousMonth() = add(Calendar.MONTH, -1)
fun Calendar.changeToNextMonth() = add(Calendar.MONTH, 1)
fun Calendar.isNotAfter(sth: Any) = !after(sth)
fun getCurrentTimeCalendar(): Calendar = Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }
fun getTimeFrom(year: Int, month: Int, day: Int): Calendar = Calendar.getInstance().apply { set(year, month, day, 12, 0) }
fun Calendar.getFullMonthName(): String = SimpleDateFormat("MMMM", Locale.UK).format(this.time)
fun Calendar.isWeekendDay(): Boolean = get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY
fun Calendar.dayName(): String = SimpleDateFormat("EEE", Locale.UK).run { format(this@dayName.time) }