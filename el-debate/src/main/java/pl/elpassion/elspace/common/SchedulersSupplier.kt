package pl.elpassion.elspace.common

import io.reactivex.Scheduler

data class SchedulersSupplier(val backgroundScheduler: Scheduler, val uiScheduler: Scheduler)