package pl.elpassion.elspace.debate.chat.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.logged_user_comment.view.*
import pl.elpassion.elspace.debate.chat.GetComment

class LoggedUserCommentHolder(itemView: View) : ViewHolderBinder<GetComment>(itemView) {

    override fun bind(item: GetComment) {
        itemView.loggedUserCommentInitials.text = item.initials
    }
}