package pl.elpassion.elspace.hub.report.list

import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class TodayPositionObserver {

    private val positionSubject = BehaviorSubject.createDefault(-1)

    val lastPosition: Int get() = positionSubject.blockingFirst()

    fun observe(): Observable<Int> = positionSubject

    fun updatePosition(position: Int) {
        positionSubject.onNext(position)
    }
}