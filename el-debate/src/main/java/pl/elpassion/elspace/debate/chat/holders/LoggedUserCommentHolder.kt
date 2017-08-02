package pl.elpassion.elspace.debate.chat.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.logged_user_comment.view.*
import pl.elpassion.elspace.debate.chat.Comment

class LoggedUserCommentHolder(itemView: View) : ViewHolderBinder<Comment>(itemView) {

    override fun bind(item: Comment) {
        itemView.loggedUserCommentInitials.text = item.initials
        itemView.loggedUserCommentName.text = item.name
    }
}