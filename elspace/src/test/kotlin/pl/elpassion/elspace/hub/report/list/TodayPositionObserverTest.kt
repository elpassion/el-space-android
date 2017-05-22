package pl.elpassion.elspace.hub.report.list

import org.junit.Assert.assertEquals
import org.junit.Test
import io.reactivex.observers.TestObserver

class TodayPositionObserverTest {

    val positionObserver = TodayPositionObserver()

    @Test
    fun shouldEmitNewTodayPositionOnUpdate() {
        val subscriber = TestObserver<Int>()
        positionObserver.observe()
                .subscribe(subscriber)

        positionObserver.updatePosition(7)

        subscriber.assertValues(-1, 7)
    }

    @Test
    fun shouldLastPositionReturnLastEmittedValue() {
        val subscriber = TestObserver<Int>()
        positionObserver.observe()
                .subscribe(subscriber)

        positionObserver.updatePosition(6)
        positionObserver.updatePosition(7)

        assertEquals(7, positionObserver.lastPosition)
    }

    @Test
    fun shouldLastPositionReturnMinusOneBeforeFirstEmittedValue() {
        val subscriber = TestObserver<Int>()
        positionObserver.observe()
                .subscribe(subscriber)

        assertEquals(-1, positionObserver.lastPosition)
    }
}