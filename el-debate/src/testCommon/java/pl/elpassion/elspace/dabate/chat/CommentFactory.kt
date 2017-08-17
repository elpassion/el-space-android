package pl.elpassion.elspace.dabate.chat

import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentToSend

fun createComment(name: String = "First Last", userId: String = "1") = Comment(fullName = name, createdAt = "456", userInitials = "FL", userInitialsBackgroundColor = "#ff3333", content = "Message", userId = userId)
fun createCommentToSend() = CommentToSend("token", "message", "first", "last")