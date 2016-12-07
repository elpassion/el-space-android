package pl.elpassion.report.list.service

import pl.elpassion.report.list.YearMonth
import rx.Observable

interface DateChangeObserver {
    fun observe(): Observable<YearMonth>

    fun setNextMonth()

    fun setPreviousMonth()
}