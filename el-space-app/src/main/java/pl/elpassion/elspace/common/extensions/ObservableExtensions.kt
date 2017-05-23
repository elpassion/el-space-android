package pl.elpassion.elspace.common.extensions

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction

fun <T> Observable<T>.catchOnError(handleOnError: (throwable: Throwable) -> Unit): Observable<T> = this
        .onErrorResumeNext { throwable: Throwable ->
            handleOnError(throwable)
            Observable.empty()
        }