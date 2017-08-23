package pl.elpassion.elspace.debate.chat

import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.*

class TimeFormatterTest {

    @Test
    fun shouldReturnCorrectTime() {
        val time = 50000000L.getTime(TimeZone.getTimeZone("GMT"))
        assertTrue(time == "13:53")
    }

    @Test
    fun shouldReturnCorrectTimeForDifferentTimeZone() {
        val time = 50000000L.getTime(TimeZone.getTimeZone("Europe/Warsaw"))
        assertTrue(time == "14:53")
    }

    @Test
    fun shouldReturnCorrectTimeForModernTimestamp() {
        val time = 1503413839000L.getTime(TimeZone.getTimeZone("GMT"))
        assertTrue(time == "14:57")
    }
}