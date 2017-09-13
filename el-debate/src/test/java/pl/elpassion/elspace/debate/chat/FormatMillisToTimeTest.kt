package pl.elpassion.elspace.debate.chat

import org.junit.Assert.assertEquals
import org.junit.Test
import pl.elpassion.elspace.common.extensions.formatMillisToTime
import java.util.*

class FormatMillisToTimeTest {

    @Test
    fun shouldReturnCorrectTime() {
        val time = 50000000L.formatMillisToTime(TimeZone.getTimeZone("GMT"))
        assertEquals(time, "13:53")
    }

    @Test
    fun shouldReturnCorrectTimeForDifferentTimeZone() {
        val time = 50000000L.formatMillisToTime(TimeZone.getTimeZone("Europe/Warsaw"))
        assertEquals(time, "14:53")
    }

    @Test
    fun shouldReturnCorrectTimeForModernTimestamp() {
        val time = 1503413839000L.formatMillisToTime(TimeZone.getTimeZone("GMT"))
        assertEquals(time, "14:57")
    }
}