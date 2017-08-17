package pl.elpassion.elspace.dabate.chat

import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentToSend

fun createCommentByLoggedUser() = Comment(fullName = "First Last", createdAt = "123", userInitials = "FO", userInitialsBackgroundColor = "#83ff33", content = "Message", userId = "1")
fun createComment(name: String = "OtherFirst OtherLast") = Comment(fullName = name, createdAt = "456", userInitials = "WX", userInitialsBackgroundColor = "#ff3333", content = "OtherMessage", userId = "2")
fun createCommentToSend() = CommentToSend("token", "message", "first", "last")