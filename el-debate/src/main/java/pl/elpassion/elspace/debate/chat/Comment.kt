package pl.elpassion.elspace.debate.chat

import com.elpassion.android.commons.recycler.basic.WithStableId

data class Comment(override val id: Long,
                   val userInitials: String,
                   val userInitialsBackgroundColor: String,
                   val fullName: String,
                   val content: String,
                   val createdAt: Long,
                   val userId: Long,
                   val status: String,
                   var wasShown: Boolean = false) : WithStableId {
    val commentStatus: CommentStatus
        get() = CommentStatus.valueOf(status.toUpperCase())
}

enum class CommentStatus {
    PENDING, ACCEPTED, REJECTED
}
