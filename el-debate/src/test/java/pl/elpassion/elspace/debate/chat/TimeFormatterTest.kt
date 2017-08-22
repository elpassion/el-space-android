package pl.elpassion.elspace.debate.chat

import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class TimeFormatterTest {

    @Test
    fun shouldReturnCorrectTime() {
        val timeFormatter = TimeFormatter(5000L, TimeZone.getTimeZone("GMT"))
        assertTrue(timeFormatter.getTime() == "01:23")
    }

    @Test
    fun shouldReturnCorrectTimeForDifferentTimeZone() {
        val timeFormatter = TimeFormatter(5000L, TimeZone.getTimeZone("Europe/Warsaw"))
        assertTrue(timeFormatter.getTime() == "02:23")
    }

    @Test
    fun shouldReturnCorrectTimeForGMT() {
        val timeFormatter = TimeFormatter(1503413839L, TimeZone.getTimeZone("GMT"))
        assertTrue(timeFormatter.getTime() == "14:57")
    }
}