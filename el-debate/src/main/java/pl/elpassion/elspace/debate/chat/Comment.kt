package pl.elpassion.elspace.debate.chat

data class Comment(val initials: String,
                   val backgroundColor: Int,
                   val name: String,
                   val message: String,
                   val isPostedByLoggedUser: Boolean)
