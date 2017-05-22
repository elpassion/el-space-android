package pl.elpassion.elspace.common.extensions

fun Double.toStringWithoutZeroes() = if (this == Math.floor(this)) "%.0f".format(this) else toString()