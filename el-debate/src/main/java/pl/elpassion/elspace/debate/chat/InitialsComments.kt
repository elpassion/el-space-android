package pl.elpassion.elspace.debate.chat

data class InitialsComments(val debateClosed: Boolean, val comments: List<Comment>, val nextPosition: Long)