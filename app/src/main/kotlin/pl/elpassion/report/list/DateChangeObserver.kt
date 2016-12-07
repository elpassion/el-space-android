package pl.elpassion.report.list

import rx.Observable

interface DateChangeObserver {
    fun observe(): Observable<YearMonth>

    fun setNextMonth()

    fun setPreviousMonth()
}