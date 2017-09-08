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
import pl.elpassion.elspace.common.extensions.logExceptionIfDebug
import pl.elpassion.elspace.common.extensions.logMessageIfDebug
import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.DebateChat
import java.net.SocketException

const val API_KEY = BuildConfig.PUSHER_API_KEY
const val CLUSTER = "mt1"
const val CHANNEL_DASHBOARD = "dashboard_channel_"
const val EVENT_COMMENT_ADDED = "comment_added"
const val CHANNEL_DASHBOARD_MULTIPLE = "dashboard_channel_multiple_"
const val EVENT_COMMENTS_ADDED = "comments_added"
const val CHANNEL_USER = "user_channel_"
const val EVENT_COMMENT_REJECTED = "comment_rejected"

class DebateChatSocketImpl : DebateChat.Socket {

    private val gson by lazy { GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create() }
    private val commentListType by lazy { object : TypeToken<List<Comment>>() {}.type }

    override fun commentsObservable(debateCode: String, userId: Long): Observable<Comment> = Observable.create<Comment> { emitter: ObservableEmitter<Comment> ->
        val pusher = Pusher(API_KEY, PusherOptions().setCluster(CLUSTER))
        connectPusher(pusher, emitter)
        val channelDashboard = pusher.subscribe("$CHANNEL_DASHBOARD$debateCode")
        channelDashboard.connectEvent(EVENT_COMMENT_ADDED, emitter)
        val channelDashboardMultiple = pusher.subscribe("$CHANNEL_DASHBOARD_MULTIPLE$debateCode")
        channelDashboardMultiple.connectEventMultiple(emitter)
        val channelUser = pusher.subscribe("$CHANNEL_USER$userId")
        channelUser.connectEvent(EVENT_COMMENT_REJECTED, emitter)
        emitter.setCancellable { pusher.disconnect() }
    }

    private fun connectPusher(pusher: Pusher, emitter: ObservableEmitter<Comment>) {
        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(connectionStateChange: ConnectionStateChange?) {
                connectionStateChange?.currentState?.name?.let { logMessageIfDebug("PUSHER ConnectionState", it) }
                if (connectionStateChange?.currentState == ConnectionState.DISCONNECTED) emitter.onError(SocketException())
            }

            override fun onError(p0: String?, p1: String?, exception: Exception?) {
                exception?.let { logExceptionIfDebug("PUSHER onError", it) }
            }
        })
    }

    private fun Channel.connectEvent(event: String, emitter: ObservableEmitter<Comment>) {
        bind(event, { channelName, eventNameLog, data ->
            logMessageIfDebug("PUSHER onEvent", "channelName: $channelName, eventNameLog: $eventNameLog, data: $data")
            if (data != null) emitter.onNext(createComment(data))
        })
    }

    private fun createComment(data: String) = gson.fromJson(data, Comment::class.java)

    private fun Channel.connectEventMultiple(emitter: ObservableEmitter<Comment>) {
        bind(EVENT_COMMENTS_ADDED, { channelName, eventNameLog, data ->
            logMessageIfDebug("PUSHER onEvent", "channelName: $channelName, eventNameLog: $eventNameLog, data: $data")
            if (data != null) createCommentList(data).forEach(emitter::onNext)
        })
    }

    private fun createCommentList(data: String): List<Comment> = gson.fromJson<List<Comment>>(data, commentListType).sortedBy { it.createdAt }
}