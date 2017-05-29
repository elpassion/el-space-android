package pl.elpassion.elspace.common.extensions

import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.functions.BiFunction

fun <T> Observable<T>.catchOnError(handleOnError: (throwable: Throwable) -> Unit): Observable<T> = this
        .onErrorResumeNext { throwable: Throwable ->
            handleOnError(throwable)
            Observable.empty()
        }

fun <T1, T2, R> combineLatest(source1: ObservableSource<T1>, source2: ObservableSource<T2>, combiner: (T1, T2) -> R)
        = Observable.combineLatest(source1, source2, BiFunction(combiner))

fun <T1, T2, R> Observable<T1>.withLatestFrom(other: ObservableSource<T2>, combiner: (T1, T2) -> R)
        = withLatestFrom(other, BiFunction(combiner))

