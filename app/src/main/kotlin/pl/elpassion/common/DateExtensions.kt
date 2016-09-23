package pl.elpassion.common

import java.util.*

fun Date.yearValue() = Calendar.getInstance().apply { time = this@yearValue }.get(Calendar.YEAR)
