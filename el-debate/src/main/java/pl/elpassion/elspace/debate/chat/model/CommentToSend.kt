package pl.elpassion.elspace.debate.chat.model

data class CommentToSend(val token: String,
                         val message: String,
                         val firstName: String,
                         val lastName: String)