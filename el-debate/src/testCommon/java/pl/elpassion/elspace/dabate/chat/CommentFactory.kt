package pl.elpassion.elspace.dabate.chat

import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentToSend
import pl.elpassion.elspace.debate.chat.InitialsComments

fun createComment(id: Long = 22, userInitials: String = "FL", name: String = "First Last", content: String = "Message", createdAt: Long = 456, userId: Long = 1, status: String = "accepted") =
        Comment(id = id, userInitials = userInitials, userInitialsBackgroundColor = "#ff3333", fullName = name, content = content, createdAt = createdAt, userId = userId, status = status)

fun createComments() = listOf(createComment())
fun createInitialsComments(debateClosed: Boolean = false, comments: List<Comment> = createComments(), nextPosition: Long = 23) =
        InitialsComments(debateClosed = debateClosed, comments = comments, nextPosition = nextPosition)

fun createCommentToSend() = CommentToSend(token = "token", message = "message", firstName = "first", lastName = "last")