package pl.elpassion.elspace.dabate.chat

import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentToSend
import pl.elpassion.elspace.debate.chat.InitialsComments
import pl.elpassion.elspace.debate.chat.SendCommentResponse

fun createComment(userInitials: String = "FL", name: String = "First Last", content: String = "Message", createdAt: Long = 456, userId: Long = 1) = Comment(fullName = name, createdAt = createdAt, userInitials = userInitials, userInitialsBackgroundColor = "#ff3333", content = content, userId = userId)
fun createComments() = listOf(createComment())
fun createInitialsComments(debateClosed: Boolean = false, comments: List<Comment> = createComments()) = InitialsComments(debateClosed = debateClosed, comments = comments)
fun createCommentToSend() = CommentToSend(token = "token", message = "message", firstName = "first", lastName = "last")
fun createSendCommentResponse(pending: Boolean = false, comment: Comment = createComment()) = SendCommentResponse(pending = pending, comment = comment)