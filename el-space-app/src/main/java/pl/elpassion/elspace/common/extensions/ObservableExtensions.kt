package pl.elpassion.elspace.common.extensions

import io.reactivex.Completable
import io.reactivex.Observable

fun <T> Observable<T>.catchOnError(handleOnError: (throwable: Throwable) -> Unit): Observable<T> = this
        .onErrorResumeNext { throwable: Throwable ->
            handleOnError(throwable)
            Observable.empty()
        }

fun Completable.catchOnError(handleOnError: (throwable: Throwable) -> Unit): Completable = this
        .doOnError(handleOnError)
        .onErrorComplete()