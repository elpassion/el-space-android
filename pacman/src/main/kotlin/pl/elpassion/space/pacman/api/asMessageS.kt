package pl.elpassion.space.pacman.api

import rx.Notification
import rx.Observable

fun Observable<WebSocketClient.Event>.asMessageS(): Observable<String> = this
        .skipWhile { it !is WebSocketClient.Event.Opened }
        .skip(1)
        .map {
            when (it) {
                is WebSocketClient.Event.Message -> Notification.createOnNext(it.body)
                is WebSocketClient.Event.Closed -> Notification.createOnCompleted()
                is WebSocketClient.Event.Failed -> Notification.createOnError(it.throwable)
                is WebSocketClient.Event.Opened -> Notification.createOnError(IllegalStateException("Second \"Opened\" event"))
            }
        }
        .dematerialize<String>()