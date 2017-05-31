package pl.elpassion.elspace.hub.report.list

import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import pl.elpassion.elspace.common.extensions.getTimeFrom
import pl.elpassion.elspace.hub.report.list.service.DateChangeObserver
import java.util.*
import java.util.Calendar.YEAR

class DateChangeObserverTest {
    val calendar: Calendar = getTimeFrom(year = 2016,
            month = 11,
            day = 10)
    val dateController = DateChangeObserver(calendar)
    private val initialValueForYearMonth = calendar.toYearMonth()

    @Test
    fun shouldEmitInitialValue() {
        assertEquals(firstIncomingDate(), initialValueForYearMonth)
    }

    @Test
    fun shouldNotSkipAnyOfEventWhenIsSubscribed() {
        val testSub = TestObserver<YearMonth>()
        dateController.observe()
                .subscribe(testSub)

        dateController.setNextMonth()
        dateController.setPreviousMonth()

        testSub.assertValueCount(3)
    }

    @Test
    fun shouldYearNotChangeAfterInitialCalendarHasChanged() {
        calendar.set(YEAR, 1000)
        assertNotEquals(firstIncomingDate().year, 1000)
    }

    private fun firstIncomingDate() = dateController.observe().blockingFirst()

}