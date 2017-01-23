package pl.elpassion.common.extensions


import rx.Observable
import rx.Subscription
import rx.subscriptions.CompositeSubscription

fun Subscription.addTo(subscription: CompositeSubscription) = subscription.add(this)

fun <T> Observable<T>.catchOnError(handleOnError: (throwable: Throwable) -> Unit): Observable<T> = this
        .onErrorResumeNext {
            handleOnError(it)
            Observable.empty()
        }
