package pl.elpassion.elspace.common

import io.reactivex.Scheduler

data class SchedulersSupplier(val subscribeOn: Scheduler, val observeOn: Scheduler)