package pl.elpassion.elspace.dabate.chat

import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentToSend

fun createCommentByLoggedUser() = Comment(fullName = "First Last", createdAt = "123", userInitials = "FO", userInitialsBackgroundColor = "#f9ceca", content = "Message", token = "tok1")
fun createComment(name: String = "OtherFirst OtherLast") = Comment(fullName = name, createdAt = "456", userInitials = "WX", userInitialsBackgroundColor = "#a3feha", content = "OtherMessage", token = "tok2")
fun createCommentToSend() = CommentToSend("token", "message", "first", "last")