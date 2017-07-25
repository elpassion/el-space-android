package pl.elpassion.elspace.dabate.details

fun createString(length: Int) = StringBuilder().apply {
    for (i in 1..length) {
        append("x")
    }
}.toString()
