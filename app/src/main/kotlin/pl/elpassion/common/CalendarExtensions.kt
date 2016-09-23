package pl.elpassion.common

import java.text.SimpleDateFormat
import java.util.*

fun Calendar.changeToPreviousMonth() = add(Calendar.MONTH, -1)
fun Calendar.changeToNextMonth() = add(Calendar.MONTH, 1)
fun Calendar.isNotAfter(sth: Any) = !after(sth)
fun getCurrentTimeCalendar(): Calendar = Calendar.getInstance().apply { time = Date(CurrentTimeProvider.get()) }
fun getTimeFrom(year: Int, month: Int, day: Int): Calendar = Calendar.getInstance().apply { set(year, month, day) }
fun Calendar.getFullMonthName() = SimpleDateFormat("MMMM", Locale.UK).format(this.time)