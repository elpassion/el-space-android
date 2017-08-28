package pl.elpassion.elspace.debate.chat.service

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import pl.elpassion.BuildConfig
import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.DebateChat
import pl.elpassion.elspace.debate.chat.printIfDebug
import java.net.SocketException

const val API_KEY = BuildConfig.PUSHER_API_KEY
const val CLUSTER = "mt1"
const val CHANNEL_NAME_PREFIX = "dashboard_channel_"
const val CHANNEL_NAME_MULTIPLE_PREFIX = "dashboard_channel_multiple_"
const val EVENT_NAME = "comment_added"
const val EVENT_NAME_MULTIPLE = "comments_added"

class DebateChatSocketImpl : DebateChat.Socket {

    private val gson by lazy { GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() }
    private val commentListType by lazy { object : TypeToken<List<Comment>>() {}.type }

    override fun commentsObservable(debateCode: String): Observable<Comment> = Observable.create<Comment> { emitter: ObservableEmitter<Comment> ->
        val pusher = Pusher(API_KEY, PusherOptions().setCluster(CLUSTER))
        connectPusher(pusher, emitter)
        val channel = pusher.subscribe("$CHANNEL_NAME_PREFIX$debateCode")
        bindToChannel(channel, emitter)
        val channelMultiple = pusher.subscribe("$CHANNEL_NAME_MULTIPLE_PREFIX$debateCode")
        bindToChannelWithMultipleEvents(channelMultiple, emitter)
        emitter.setCancellable { pusher.disconnect() }
    }

    private fun connectPusher(pusher: Pusher, emitter: ObservableEmitter<Comment>) {
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(connectionStateChange: ConnectionStateChange?) {
                "PUSHER ConnectionState ${connectionStateChange?.currentState?.name}".printIfDebug()
                if (connectionStateChange?.currentState == ConnectionState.DISCONNECTED) emitter.onError(SocketException())
            }

            override fun onError(p0: String?, p1: String?, exception: Exception?) {
                "PUSHER onError, p0: $p0, p1: $p1, p2: ${exception?.message}".printIfDebug()
            }
        })
    }

    private fun bindToChannel(channel: Channel, emitter: ObservableEmitter<Comment>) {
        channel.bind(EVENT_NAME, { channelName, eventName, data ->
            "PUSHER onEvent, channelName: $channelName, eventName: $eventName, data: $data".printIfDebug()
            if (data != null) emitter.onNext(createComment(data))
        })
    }

    private fun createComment(data: String) = gson.fromJson(data, Comment::class.java)

    private fun bindToChannelWithMultipleEvents(channel: Channel, emitter: ObservableEmitter<Comment>) {
        channel.bind(EVENT_NAME_MULTIPLE, { channelName, eventName, data ->
            "PUSHER onEvent, channelName: $channelName, eventName: $eventName, data: $data".printIfDebug()
            if (data != null) createCommentList(data).forEach(emitter::onNext)
        })
    }

    private fun createCommentList(data: String): List<Comment> = gson.fromJson<List<Comment>>(data, commentListType)
}