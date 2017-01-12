package pl.elpassion.space.pacman

import rx.subjects.BehaviorSubject

class WebSocketClient(private val url: String) {

    val messages = BehaviorSubject.create<String>()

}