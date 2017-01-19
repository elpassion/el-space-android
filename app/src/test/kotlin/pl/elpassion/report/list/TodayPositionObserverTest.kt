package pl.elpassion.report.list

import org.junit.Assert.assertEquals
import org.junit.Test
import rx.observers.TestSubscriber

class TodayPositionObserverTest {

    val positionObserver = TodayPositionObserver()

    @Test
    fun shouldEmitNewTodayPositionOnUpdate() {
        val subscriber = TestSubscriber<Int>()
        positionObserver.observe()
                .subscribe(subscriber)

        positionObserver.updatePosition(7)

        subscriber.assertValues(-1, 7)
    }

    @Test
    fun shouldLastPositionReturnLastEmittedValue() {
        val subscriber = TestSubscriber<Int>()
        positionObserver.observe()
                .subscribe(subscriber)

        positionObserver.updatePosition(6)
        positionObserver.updatePosition(7)

        assertEquals(positionObserver.lastPosition, 7)
    }

    @Test
    fun shouldLastPositionReturnMinusOneBeforeFirstEmittedValue() {
        val subscriber = TestSubscriber<Int>()
        positionObserver.observe()
                .subscribe(subscriber)

        assertEquals(positionObserver.lastPosition, -1)
    }
}