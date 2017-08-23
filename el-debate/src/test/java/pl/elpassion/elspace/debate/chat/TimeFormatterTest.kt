package pl.elpassion.elspace.debate.chat

import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class TimeFormatterTest {

    @Test
    fun shouldReturnCorrectTime() {
        val time = 5000L.getTime(TimeZone.getTimeZone("GMT"))
        assertTrue(time == "01:23")
    }

    @Test
    fun shouldReturnCorrectTimeForDifferentTimeZone() {
        val time = 5000L.getTime(TimeZone.getTimeZone("Europe/Warsaw"))
        assertTrue(time == "02:23")
    }

    @Test
    fun shouldReturnCorrectTimeForModernTimestamp() {
        val time = 1503413839L.getTime(TimeZone.getTimeZone("GMT"))
        assertTrue(time == "14:57")
    }
}