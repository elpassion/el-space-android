package pl.elpassion.elspace.common

import io.reactivex.observers.TestObserver

fun <T> TestObserver<T>.assertOnFirstElement(assertion: (T) -> Unit) {
    this.values().first().run(assertion)
}
