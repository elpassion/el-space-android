package pl.elpassion.elspace.common

import rx.Scheduler

data class SchedulersSupplier(val subscribeOn: Scheduler)