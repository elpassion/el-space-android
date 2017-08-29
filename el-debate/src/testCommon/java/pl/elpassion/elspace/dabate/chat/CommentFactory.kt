package pl.elpassion.elspace.dabate.chat

import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentToSend

fun createComment(userInitials: String = "FL", name: String = "First Last", content: String = "Message", createdAt: Long = 456, userId: Long = 1) = Comment(fullName = name, createdAt = createdAt, userInitials = userInitials, userInitialsBackgroundColor = "#ff3333", content = content, userId = userId)
fun createCommentToSend() = CommentToSend("token", "message", "first", "last")