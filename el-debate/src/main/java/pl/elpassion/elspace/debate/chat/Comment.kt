package pl.elpassion.elspace.debate.chat

data class Comment(val userInitials: String,
                   val userInitialsBackgroundColor: String,
                   val fullName: String,
                   val content: String,
                   val createdAt: Long,
                   val userId: Long)
