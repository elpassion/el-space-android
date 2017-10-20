package pl.elpassion.elspace.debate.chat.holders

import android.graphics.Color
import android.graphics.PorterDuff
import android.support.annotation.StringRes
import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import com.elpassion.android.view.hide
import com.elpassion.android.view.show
import kotlinx.android.synthetic.main.logged_user_comment.view.*
import pl.elpassion.R
import pl.elpassion.elspace.common.extensions.formatMillisToTime
import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.CommentStatus
import java.util.*

class LoggedUserCommentHolder(itemView: View, private val timeZone: () -> TimeZone) : ViewHolderBinder<Comment>(itemView) {

    override fun bind(item: Comment) {
        itemView.run {
            loggedUserCommentInitials.background.setColorFilter(Color.parseColor(item.userInitialsBackgroundColor), PorterDuff.Mode.SRC_IN)
            loggedUserCommentInitials.text = item.userInitials
            loggedUserCommentName.text = item.fullName
            loggedUserCommentMessage.text = item.content
            loggedUserCommentTime.text = item.createdAt.formatMillisToTime(timeZone)
            when (item.commentStatus) {
                CommentStatus.PENDING -> showCommentStatusWithText(R.string.debate_chat_comment_status_pending)
                CommentStatus.ACCEPTED -> loggedUserCommentStatus.hide()
                CommentStatus.REJECTED -> showCommentStatusWithText(R.string.debate_chat_comment_status_rejected)
            }
        }
    }

    private fun showCommentStatusWithText(@StringRes resource: Int) {
        itemView.loggedUserCommentStatus.show()
        itemView.loggedUserCommentStatus.text = itemView.context.getString(resource)
    }
}