package pl.elpassion.elspace.debate.chat

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import pl.elpassion.BuildConfig
import java.net.SocketException

const val API_KEY = "###"
const val CLUSTER = "eu"
const val CHANNEL_NAME_PREFIX = "dashboard_channel_"
const val EVENT_NAME = "comment_added"

class DebateChatSocketImpl : DebateChat.Socket {

    override fun commentsObservable(debateCode: String): Observable<Comment> = Observable.create<Comment> { emitter: ObservableEmitter<Comment> ->
        val pusher = Pusher(API_KEY, PusherOptions().setCluster(CLUSTER))
        connectPusher(pusher, emitter)
        val channel = pusher.subscribe("$CHANNEL_NAME_PREFIX$debateCode")
        bindToChannel(channel, emitter)
        emitter.setCancellable { pusher.disconnect() }
    }

    private fun connectPusher(pusher: Pusher, emitter: ObservableEmitter<Comment>) {
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(connectionStateChange: ConnectionStateChange?) {
                if (BuildConfig.DEBUG) Log.i("PUSHER ConnectionState", connectionStateChange?.currentState?.name)
                if (connectionStateChange?.currentState == ConnectionState.DISCONNECTED) emitter.onError(SocketException())
            }

            override fun onError(p0: String?, p1: String?, exception: Exception?) {
                if (BuildConfig.DEBUG) Log.e("PUSHER onError", "p0: $p0, p1: $p1, p2: ${exception?.message}")
            }
        })
    }

    private fun bindToChannel(channel: Channel, emitter: ObservableEmitter<Comment>) {
        channel.bind(EVENT_NAME, { channelName, eventName, data ->
            if (BuildConfig.DEBUG) Log.i("PUSHER onEvent", "channelName: $channelName, eventName: $eventName, data: $data")
            if (data != null) emitter.onNext(createComment(data))
        })
    }

    private fun createComment(data: String) =
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create().run {
                fromJson(data, Comment::class.java)
            }
}