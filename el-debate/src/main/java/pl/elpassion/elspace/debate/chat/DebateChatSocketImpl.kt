package pl.elpassion.elspace.debate.chat

import android.util.Log
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionStateChange
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import pl.elpassion.BuildConfig

class DebateChatSocketImpl : DebateChat.Socket {

    override fun commentsObservable(debateCode: String): Observable<Comment> = Observable.create<Comment> { emitter: ObservableEmitter<Comment> ->
        val pusherOptions = PusherOptions().apply { setCluster("eu") }
        val pusher = Pusher("###", pusherOptions)

        if (BuildConfig.DEBUG) connectPusherDebug(pusher) else pusher.connect()

        val channel = pusher.subscribe("my-channel")

        channel.bind("my-event", { channelName, eventName, data ->
            if (BuildConfig.DEBUG) {
                Log.i("PUSHER onEvent", "channelName: $channelName, eventName: $eventName, data: $data")
            }
            data?.let {
                val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
                val comment = gson.fromJson(it, Comment::class.java)
                emitter.onNext(comment)
            }
        })

        emitter.setCancellable { pusher.disconnect() }
    }

    private fun connectPusherDebug(pusher: Pusher) {
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(connectionStateChange: ConnectionStateChange?) {
                Log.i("PUSHER ConnectionState", connectionStateChange?.currentState?.name)
            }

            override fun onError(p0: String?, p1: String?, exception: Exception?) {
                Log.e("PUSHER onError", "p0: $p0, p1: $p1, p2: ${exception?.message}")
            }
        })
    }
}