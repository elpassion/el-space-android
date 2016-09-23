package pl.elpassion.common

import java.util.*

fun Date.yearValue() = Calendar.getInstance().apply { time = this@yearValue }.get(Calendar.YEAR)
fun Date.monthValue() = Calendar.getInstance().apply { time = this@monthValue }.get(Calendar.MONTH) + 1
fun Date.dayValue() = Calendar.getInstance().apply { time = this@dayValue }.get(Calendar.DAY_OF_MONTH)
