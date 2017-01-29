package pl.elpassion.elspace.hub.report.list

import rx.Observable
import rx.subjects.BehaviorSubject

class TodayPositionObserver {

    private val positionSubject = BehaviorSubject.create(-1)

    val lastPosition: Int get() = positionSubject.toBlocking().first()

    fun observe(): Observable<Int> = positionSubject.asObservable()

    fun updatePosition(position: Int) {
        positionSubject.onNext(position)
    }
}