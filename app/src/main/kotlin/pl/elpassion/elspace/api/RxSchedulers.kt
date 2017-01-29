package pl.elpassion.elspace.api

import rx.Completable
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

fun <T> Observable<T>.applySchedulers(): Observable<T> =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


fun Completable.applySchedulers(): Completable =
        this.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
