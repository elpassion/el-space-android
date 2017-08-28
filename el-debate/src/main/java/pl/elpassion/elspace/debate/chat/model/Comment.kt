package pl.elpassion.elspace.debate.chat.model

data class Comment(val userInitials: String,
                   val userInitialsBackgroundColor: String,
                   val fullName: String,
                   val content: String,
                   val createdAt: Long,
                   val userId: Long)
