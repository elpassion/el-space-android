package pl.elpassion.elspace.debate.chat

data class Comment(val userInitials: String,
                   val createdAt: String,
                   val userInitialsBackgroundColor: String,
                   val fullName: String,
                   val content: String,
                   val token: String)
