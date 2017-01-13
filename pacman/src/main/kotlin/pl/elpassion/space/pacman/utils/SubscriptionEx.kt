package pl.elpassion.space.pacman.utils

import rx.Completable
import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription


fun Subscription.save(to: CompositeSubscription) = to.add(this)

fun <T> Observable<T>.completeOnError(action: (Throwable) -> Unit) = this
        .doOnError { action(it) }
        .onErrorResumeNext(Observable.empty())

fun  Completable.completeOnError(action: (Throwable) -> Unit) = this
        .doOnError { action(it) }
        .onErrorResumeNext { Completable.complete() }


