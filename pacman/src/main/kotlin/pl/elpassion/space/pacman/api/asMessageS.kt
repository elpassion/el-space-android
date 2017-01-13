package pl.elpassion.space.pacman.api

import rx.Notification
import rx.Observable

fun Observable<WebSocketClientImpl.Event>.asMessageS(): Observable<String> = this
        .skipWhile { it !is WebSocketClientImpl.Event.Opened }
        .skip(1)
        .map {
            when (it) {
                is WebSocketClientImpl.Event.Message -> Notification.createOnNext(it.body)
                is WebSocketClientImpl.Event.Closed -> Notification.createOnCompleted()
                is WebSocketClientImpl.Event.Failed -> Notification.createOnError(it.throwable)
                is WebSocketClientImpl.Event.Opened -> Notification.createOnError(IllegalStateException("Second \"Opened\" event"))
            }
        }
        .dematerialize<String>()