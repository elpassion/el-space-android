package pl.elpassion.elspace.dabate.chat

import pl.elpassion.elspace.debate.chat.Comment

fun createCommentByLoggedUser() = Comment(name = "First Last", initials = "FO", backgroundColor = 0xFFFF0000.toInt(), message = "Message", isPostedByLoggedUser = true)
fun createComment(name: String = "OtherFirst OtherLast") = Comment(name = name, initials = "WX", backgroundColor = 0xFF0000FF.toInt(), message = "OtherMessage", isPostedByLoggedUser = false)