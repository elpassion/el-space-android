package pl.elpassion.elspace.debate.chat

data class Comment(val userInitials: String,
                   val createdAt: Long,
                   val userInitialsBackgroundColor: String,
                   val fullName: String,
                   val content: String,
                   val userId: Long)
