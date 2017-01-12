package pl.elpassion.space

import org.junit.Assert
import rx.Observable
import rx.subjects.PublishSubject

class SubscriptionSubjectVerifier<T> {
    private val subject = PublishSubject.create<T>()
    private var wasSubscribed: Boolean = false
    private var wasUnsubscribed: Boolean = false

    val observable: Observable<T> = subject.doOnSubscribe {
        wasSubscribed = true
    }.doOnUnsubscribe {
        wasUnsubscribed = true
    }

    fun onNext(v: T) {
        subject.onNext(v)
    }

    fun onCompleted() {
        subject.onCompleted()
    }

    fun onError(throwable: Throwable) {
        subject.onError(throwable)
    }

    fun assertSubscribe() {
        Assert.assertTrue(wasSubscribed)
    }

    fun assertUnsubscribe() {
        Assert.assertTrue(wasUnsubscribed)
    }
}