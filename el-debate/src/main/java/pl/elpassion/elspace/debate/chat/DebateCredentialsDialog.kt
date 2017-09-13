package pl.elpassion.elspace.debate.chat

import android.content.Context
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.credentials_dialog_layout.view.*
import pl.elpassion.R

class DebateCredentialsDialog(private val context: Context, private val onNewCredentials: (TokenCredentials) -> Unit) {

    private val credentialsDialogView by lazy {
        LayoutInflater.from(context).inflate(R.layout.credentials_dialog_layout, null).apply {
            debateCommentCredentialsConfirm.setOnClickListener {
                val credentials = TokenCredentials(firstName = debateCredentialsFirstNameInputText.text.toString(), lastName = debateCredentialsLastNameInputText.text.toString())
                onNewCredentials(credentials)
            }
        }
    }

    private val credentialsDialog by lazy {
        AlertDialog.Builder(context)
                .setView(credentialsDialogView)
                .create()
    }

    fun show() = credentialsDialog.show()

    fun hide() = credentialsDialog.hide()

    fun dismiss() = credentialsDialog.dismiss()

    fun showFirstNameError() {
        credentialsDialogView.debateCredentialsFirstNameLayout.error = context.getString(R.string.debate_chat_credentials_first_name_incorrect)
    }

    fun showLastNameError() {
        credentialsDialogView.debateCredentialsLastNameLayout.error = context.getString(R.string.debate_chat_credentials_last_name_incorrect)
    }

}