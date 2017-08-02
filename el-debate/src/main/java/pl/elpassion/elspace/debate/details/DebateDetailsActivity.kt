package pl.elpassion.elspace.debate.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.elpassion.android.view.disable
import com.elpassion.android.view.enable
import com.elpassion.android.view.show
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.debate_details_activity.*
import kotlinx.android.synthetic.main.debate_toolbar.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.extensions.handleClickOnBackArrowItem
import pl.elpassion.elspace.common.extensions.showBackArrowOnActionBar
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import pl.elpassion.elspace.debate.chat.DebateChatActivity


class DebateDetailsActivity : AppCompatActivity(), DebateDetails.View {

    private val controller by lazy {
        DebateDetailsController(DebateDetails.ApiProvider.get(), this, SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    private val token by lazy { intent.getStringExtra(debateAuthTokenKey) }

    private val answersAnimators by lazy { AnswersAnimators(debateDetailsCoordinator, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_details_activity)
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        debateChatButton.setOnClickListener { controller.onChat() }
        controller.onCreate(token)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = handleClickOnBackArrowItem(item)

    override fun showDebateDetails(debateDetails: DebateData) {
        debateDetails.run {
            debateTopic.text = topic
            debatePositiveAnswerText.text = answers.positive.value
            debateNegativeAnswerText.text = answers.negative.value
            debateNeutralAnswerText.text = answers.neutral.value
            when (lastAnswerId) {
                answers.positive.id -> answersAnimators.startPositiveAnswerAnimation()
                answers.negative.id -> answersAnimators.startNegativeAnswerAnimation()
                answers.neutral.id -> answersAnimators.startNeutralAnswerAnimation()
            }
            debatePositiveAnswerButton.setOnClickListener {
                controller.onVote(token, answers.positive)
            }
            debateNegativeAnswerButton.setOnClickListener {
                controller.onVote(token, answers.negative)
            }
            debateNeutralAnswerButton.setOnClickListener {
                controller.onVote(token, answers.neutral)
            }
        }
    }

    override fun showLoader() = showLoader(debateDetailsCoordinator)

    override fun hideLoader() = hideLoader(debateDetailsCoordinator)

    override fun showDebateClosedError() {
        debateClosedView.show()
    }

    override fun showDebateDetailsError(exception: Throwable) {
        createSnackbar(R.string.debate_details_error)
                .setAction(R.string.debate_details_error_refresh, { controller.onDebateDetailsRefresh(token) })
                .show()
    }

    override fun showVoteLoader(answer: Answer) {
        disableVoteButtons()
        getLoaderForAnswer(answer).show()
    }

    private fun disableVoteButtons() {
        debateNegativeAnswerButton.disable()
        debateNeutralAnswerButton.disable()
        debatePositiveAnswerButton.disable()
    }

    private fun getLoaderForAnswer(answer: Answer) = when (answer) {
        is Positive -> debatePositiveAnswerLoader
        is Negative -> debateNegativeAnswerLoader
        is Neutral -> debateNeutralAnswerLoader
    }

    override fun hideVoteLoader() {
        debatePositiveAnswerLoader.hide { enableVoteButtons() }
        debateNeutralAnswerLoader.hide { enableVoteButtons() }
        debateNegativeAnswerLoader.hide { enableVoteButtons() }
    }

    fun enableVoteButtons() {
        debateNegativeAnswerButton.enable()
        debateNeutralAnswerButton.enable()
        debatePositiveAnswerButton.enable()
    }

    override fun showVoteError(exception: Throwable) {
        createSnackbar(R.string.debate_details_vote_error).show()
    }

    override fun showVoteSuccess(answer: Answer) {
        createSnackbar(R.string.debate_details_vote_success, Snackbar.LENGTH_SHORT).show()
        when (answer) {
            is Positive -> answersAnimators.startPositiveAnswerAnimation()
            is Negative -> answersAnimators.startNegativeAnswerAnimation()
            is Neutral -> answersAnimators.startNeutralAnswerAnimation()
        }
    }

    private fun createSnackbar(textId: Int, length: Int = Snackbar.LENGTH_INDEFINITE) =
            Snackbar.make(debateDetailsCoordinator, textId, length)

    override fun openChatScreen() {
        DebateChatActivity.start(this, token)
    }

    override fun showSlowDownInformation() {
        AlertDialog.Builder(this)
                .setTitle(R.string.debate_details_vote_slow_down_title)
                .setMessage(R.string.debate_details_vote_slow_down_info)
                .setPositiveButton(R.string.debate_details_vote_slow_down_OK_button, { dialog, _ -> dialog.dismiss() })
                .create()
                .show()
    }

    override fun onDestroy() {
        controller.onDestroy()
        super.onDestroy()
    }

    companion object {

        private val debateAuthTokenKey = "debateAuthTokenKey"

        fun start(context: Context, debateToken: String) = context.startActivity(intent(context, debateToken))

        fun intent(context: Context, debateToken: String) =
                Intent(context, DebateDetailsActivity::class.java).apply {
                    putExtra(debateAuthTokenKey, debateToken)
                }
    }

}