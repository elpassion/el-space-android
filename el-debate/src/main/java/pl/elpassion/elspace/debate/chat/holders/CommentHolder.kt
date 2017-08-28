package pl.elpassion.elspace.debate.chat.holders

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.comment.view.*
import pl.elpassion.elspace.debate.chat.getTime
import pl.elpassion.elspace.debate.chat.model.Comment

class CommentHolder(itemView: View) : ViewHolderBinder<Comment>(itemView) {

    override fun bind(item: Comment) {
        itemView.commentInitials.background.setColorFilter(Color.parseColor(item.userInitialsBackgroundColor), PorterDuff.Mode.SRC_IN)
        itemView.commentInitials.text = item.userInitials
        itemView.commentName.text = item.fullName
        itemView.commentMessage.text = item.content
        itemView.commentTime.text = item.createdAt.getTime()
    }
}