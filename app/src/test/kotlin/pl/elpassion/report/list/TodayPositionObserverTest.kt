package pl.elpassion.report.list

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

        subscriber.assertValue(7)
    }
}