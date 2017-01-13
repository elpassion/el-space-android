package pl.elpassion.space.pacman.utils

import rx.Subscription
import rx.subscriptions.CompositeSubscription


fun Subscription.save(to: CompositeSubscription) = to.add(this)
