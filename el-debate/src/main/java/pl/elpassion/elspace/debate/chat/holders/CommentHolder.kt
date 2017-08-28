package pl.elpassion.elspace.debate.chat.holders

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.comment.view.*
import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.getTime

class CommentHolder(itemView: View) : ViewHolderBinder<Comment>(itemView) {

    override fun bind(item: Comment) {
        itemView.run {
            commentInitials.background.setColorFilter(Color.parseColor(item.userInitialsBackgroundColor), PorterDuff.Mode.SRC_IN)
            commentInitials.text = item.userInitials
            commentName.text = item.fullName
            commentMessage.text = item.content
            commentTime.text = item.createdAt.getTime()
        }
    }
}