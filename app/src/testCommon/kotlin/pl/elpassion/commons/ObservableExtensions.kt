package pl.elpassion.commons

import rx.Observable

fun <T> apiRuntimeError(): Observable<T> = Observable.error(RuntimeException())

