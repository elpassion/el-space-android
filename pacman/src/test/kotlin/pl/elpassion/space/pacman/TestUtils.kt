package pl.elpassion.space.pacman

import org.junit.Assert.assertEquals
import rx.Observer
import rx.observers.TestSubscriber

fun <T> TestSubscriber<T>.assertValueThat(predicate: (T) -> Boolean) {
    val events = onNextEvents
    assertEquals(events.size, 1)
    assert(predicate(events.first()))
}

fun <T> TestSubscriber<T>.assertLastValueThat(predicate: (T) -> Boolean) = assert(predicate(onNextEvents.last()))

fun <T> Observer<T>.onNext(vararg items: T) = items.forEach { onNext(it) }
