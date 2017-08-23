package pl.elpassion.elspace.debate.chat.holders

import android.graphics.Color
import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.comment.view.*
import pl.elpassion.elspace.debate.chat.Comment
import pl.elpassion.elspace.debate.chat.getTime

class CommentHolder(itemView: View) : ViewHolderBinder<Comment>(itemView) {

    override fun bind(item: Comment) {
        itemView.commentInitials.setBackgroundColor(Color.parseColor(item.userInitialsBackgroundColor))
        itemView.commentInitials.text = item.userInitials
        itemView.commentName.text = item.fullName
        itemView.commentMessage.text = item.content
        itemView.commentMessage.text = item.createdAt.getTime()
    }
}