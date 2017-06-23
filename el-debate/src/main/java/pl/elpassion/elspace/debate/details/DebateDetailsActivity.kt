package pl.elpassion.elspace.debate.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
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
import pl.elpassion.elspace.debate.comment.DebateCommentActivity


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
        debateCommentButton.setOnClickListener { controller.onComment() }
        controller.onCreate(token)
        setupLoaderColors()
    }

    private fun setupLoaderColors() {
        debatePositiveAnswerLoader.setColor(R.color.blueDebatePositive)
        debateNegativeAnswerLoader.setColor(R.color.redDebateNegative)
        debateNeutralAnswerLoader.setColor(R.color.greyDebateNeutral)
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

    override fun showDebateDetailsError(exception: Throwable) {
        showSnackbar(R.string.debate_details_error)
    }

    override fun showVoteLoader(answer: Answer) {
        disableVoteButtons()
        getLoaderForAnswer(answer).show()
    }

    private fun disableVoteButtons() {
        debateNegativeAnswerButton.isEnabled = false
        debateNeutralAnswerButton.isEnabled = false
        debatePositiveAnswerButton.isEnabled = false
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
        debateNegativeAnswerButton.isEnabled = true
        debateNeutralAnswerButton.isEnabled = true
        debatePositiveAnswerButton.isEnabled = true
    }

    override fun showVoteError(exception: Throwable) {
        showSnackbar(R.string.debate_details_vote_error)
    }

    override fun showVoteSuccess(answer: Answer) {
        showSnackbar(R.string.debate_details_vote_success, Snackbar.LENGTH_SHORT)
        when (answer) {
            is Positive -> answersAnimators.startPositiveAnswerAnimation()
            is Negative -> answersAnimators.startNegativeAnswerAnimation()
            is Neutral -> answersAnimators.startNeutralAnswerAnimation()
        }
    }

    private fun showSnackbar(textId: Int, length: Int = Snackbar.LENGTH_INDEFINITE) {
        Snackbar.make(debateDetailsCoordinator, textId, length).show()
    }

    override fun openCommentScreen() {
        DebateCommentActivity.start(this, token)
    }

    override fun showSlowDownInformation() {
        AlertDialog.Builder(this)
                .setTitle(R.string.debate_vote_slow_down_title)
                .setMessage(R.string.debate_vote_slow_down_info)
                .setPositiveButton(R.string.debate_vote_slow_down_OK_button, { dialog, _ -> dialog.dismiss() })
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