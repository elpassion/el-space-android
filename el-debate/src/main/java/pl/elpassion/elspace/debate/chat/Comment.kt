package pl.elpassion.elspace.debate.chat

data class Comment(val userInitials: String,
                   val userInitialsBackgroundColor: String,
                   val fullName: String,
                   val content: String,
                   val createdAt: Long,
                   val userId: Long,
                   val status: String) {
    val commentStatus: CommentStatus
        get() = CommentStatus.valueOf(status.toUpperCase())
}

enum class CommentStatus {
    PENDING, ACCEPTED, REJECTED
}
