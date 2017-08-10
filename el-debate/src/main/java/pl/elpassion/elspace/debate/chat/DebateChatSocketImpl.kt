package pl.elpassion.elspace.debate.chat

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionStateChange
import io.reactivex.Observable
import io.reactivex.ObservableEmitter

class DebateChatSocketImpl : DebateChat.Socket {

    private val pusherOptions by lazy { PusherOptions().apply { setCluster("eu") } }
    private val pusher by lazy { Pusher("###", pusherOptions) }

    override fun commentsObservable(debateCode: String): Observable<Comment> = Observable.create<Comment> { emitter: ObservableEmitter<Comment> ->

        val channelListener = SubscriptionEventListener { channelName, eventName, data ->
            val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
            val comment = gson.fromJson(data, Comment::class.java)
            emitter.onNext(comment)
            Log.i("onEvent", "channelName: $channelName, eventName: $eventName, data: $data")
        }

        val connectionListener = object : ConnectionEventListener {
            override fun onConnectionStateChange(p0: ConnectionStateChange) {
                Log.i("onConnectionState: ", p0.currentState.name)
            }

            override fun onError(p0: String, p1: String, exception: Exception) {
                Log.i("onError", "p0: $p0, p1: $p1, p2: ${exception.message}")
                emitter.onError(exception)
                pusher.disconnect()
            }
        }

        pusher.connect(connectionListener)
        val channel = pusher.subscribe("dashboard_channel_$debateCode")
        channel.bind("comment_added", channelListener)

        emitter.setCancellable { pusher.disconnect() }
    }
}