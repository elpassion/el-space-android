package pl.elpassion.report.list

import rx.Observable
import rx.subjects.BehaviorSubject

class TodayPositionObserver {

    private val positionSubject = BehaviorSubject.create<Int>()

    val lastPosition: Int get() = observe().toBlocking().first()

    fun observe(): Observable<Int> = positionSubject.asObservable()

    fun updatePosition(position: Int) {
        positionSubject.onNext(position)
    }
}