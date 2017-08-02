package pl.elpassion.elspace.debate.chat.holders

import android.view.View
import com.elpassion.android.commons.recycler.basic.ViewHolderBinder
import kotlinx.android.synthetic.main.comment.view.*
import pl.elpassion.elspace.debate.chat.Comment

class CommentHolder(itemView: View) : ViewHolderBinder<Comment>(itemView) {

    override fun bind(item: Comment) {
        itemView.commentInitials.text = item.initials
        itemView.commentName.text = item.name
        itemView.commentMessage.text = item.message
    }
}