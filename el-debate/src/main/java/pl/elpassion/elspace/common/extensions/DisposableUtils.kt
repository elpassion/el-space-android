package pl.elpassion.elspace.common.extensions

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(subscription: CompositeDisposable) = subscription.add(this)