package pl.elpassion.elspace.debate.chat.holders

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.logged_user_comment.view.*
import pl.elpassion.elspace.debate.chat.getTime
import pl.elpassion.elspace.debate.chat.model.Comment

class LoggedUserCommentHolder(itemView: View) : ViewHolderBinder<Comment>(itemView) {

    override fun bind(item: Comment) {
        itemView.loggedUserCommentInitials.background.setColorFilter(Color.parseColor(item.userInitialsBackgroundColor), PorterDuff.Mode.SRC_IN)
        itemView.loggedUserCommentInitials.text = item.userInitials
        itemView.loggedUserCommentName.text = item.fullName
        itemView.loggedUserCommentMessage.text = item.content
        itemView.loggedUserCommentTime.text = item.createdAt.getTime()
    }
}