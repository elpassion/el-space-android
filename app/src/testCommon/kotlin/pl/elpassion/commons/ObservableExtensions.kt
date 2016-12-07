package pl.elpassion.commons

import rx.Observable
import rx.observers.TestSubscriber

fun <T> apiRuntimeError(): Observable<T> = Observable.error(RuntimeException())

fun <T> Observable<T>.assertNoErrors() = assert { assertNoErrors() }

fun <T> Observable<T>.assertValues(vararg values: T) = assert { assertValues(*values) }

fun <T> Observable<T>.assertValue(value: T) = assert { assertValue(value) }

fun <T> Observable<T>.assertValueCount(count: Int) = assert { assertValueCount(count) }

fun <T> Observable<T>.assertError(throwable: Throwable) = assert { assertError(throwable) }

fun <T> Observable<T>.assert(f: TestSubscriber<T>.() -> Unit): Observable<T> = apply {
    val testSub = TestSubscriber<T>()
    subscribe(testSub)
    testSub.f()
}
