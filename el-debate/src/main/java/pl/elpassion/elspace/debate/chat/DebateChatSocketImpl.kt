package pl.elpassion.elspace.debate.chat

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionStateChange
import io.reactivex.Observable

class DebateChatSocketImpl : DebateChat.Socket {

    private val pusherOptions by lazy { PusherOptions().apply { setCluster("eu") } }
    private val pusher by lazy { Pusher("###", pusherOptions) }
    private var connectionEventListener: ConnectionEventListener? = null

    override fun commentsObservable(debateCode: String): Observable<Comment> {
        val channel = pusher.subscribe("dashboard_channel_$debateCode")
        pusher.connect(connectionEventListener)
        return bindObservable(channel)
    }

    fun bindObservable(channel: Channel): Observable<Comment> = Observable.create<Comment> { emitter ->
        connectionEventListener = object : ConnectionEventListener {
            override fun onConnectionStateChange(p0: ConnectionStateChange) {
                Log.i("onConnectionState: ", p0.currentState.name)
            }

            override fun onError(p0: String, p1: String, exception: Exception) {
                Log.i("onError", "p0: $p0, p1: $p1, p2: ${exception.message}")
                emitter.onError(exception)
            }
        }
        channel.bind("comment_added") { channelName, eventName, data ->
            emitter.onNext(Comment(initials = "AA", backgroundColor = 123, name = "DD", message = data, isPostedByLoggedUser = false))
            Log.i("onEvent", "channelName: $channelName, eventName: $eventName, data: $data")
        }
    }
}