package pl.elpassion.elspace.common.extensions

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.rxkotlin.withLatestFrom

fun <T> Observable<T>.catchOnError(handleOnError: (throwable: Throwable) -> Unit): Observable<T> = this
        .onErrorResumeNext { throwable: Throwable ->
            handleOnError(throwable)
            Observable.empty()
        }

fun Completable.catchOnError(handleOnError: (throwable: Throwable) -> Unit): Completable = this
        .doOnError(handleOnError)
        .onErrorComplete()

fun <T, UiState> Observable<T>.mapToWithLastFrom(stream: Observable<UiState>, action: T.(UiState) -> UiState): Observable<UiState> =
        withLatestFrom(stream, { x, y -> x.action(y) })

fun <T, UiState> Observable<T>.mapToLastFrom(stream: Observable<UiState>): Observable<UiState> =
        withLatestFrom(stream, { _, y -> y })

infix fun <T> Observable<T>.andThen(nextObservable: Observable<T>): Observable<T> = this.concatWith(nextObservable)