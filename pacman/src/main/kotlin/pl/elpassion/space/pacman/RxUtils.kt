package pl.elpassion.space.pacman

import pl.elpassion.space.pacman.WebSocketClient.Event
import pl.elpassion.space.pacman.WebSocketClient.Event.*
import rx.Notification
import rx.Observable

fun Observable<Event>.asMessageS(): Observable<String> = this
        .skipWhile { it !is Opened }
        .skip(1)
        .map {
            when (it) {
                is Message -> Notification.createOnNext(it.body)
                is Closed -> Notification.createOnCompleted()
                is Failed -> Notification.createOnError(it.throwable)
                is Opened -> Notification.createOnError(IllegalStateException("Second \"Opened\" event"))
            }
        }
        .dematerialize<String>()


