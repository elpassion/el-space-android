package pl.elpassion.elspace.debate.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
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

    companion object {
        private val debateAuthTokenKey = "debateAuthTokenKey"

        fun start(context: Context, debateToken: String) = context.startActivity(intent(context, debateToken))

        fun intent(context: Context, debateToken: String) =
                Intent(context, DebateDetailsActivity::class.java).apply {
                    putExtra(debateAuthTokenKey, debateToken)
                }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_details_activity)
        setSupportActionBar(toolbar)
        showBackArrowOnActionBar()
        debateCommentButton.setOnClickListener { controller.onComment() }
        controller.onCreate(token)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = handleClickOnBackArrowItem(item)

    override fun onDestroy() {
        controller.onDestroy()
        super.onDestroy()
    }

    override fun showDebateDetails(debateDetails: DebateData) {
        debateDetails.run {
            when (lastAnswerId) {
                answers.positive.id -> changeImagesInButtonsOnPositiveAnswer()
                answers.negative.id -> changeImagesInButtonsOnNegativeAnswer()
                answers.neutral.id -> changeImagesInButtonsOnNeutralAnswer()
            }
        }
        debateTopic.text = debateDetails.topic
        debatePositiveAnswerText.text = debateDetails.answers.positive.value
        debatePositiveAnswerButton.setOnClickListener {
            changeImagesInButtonsOnPositiveAnswer()
            controller.onVote(token, debateDetails.answers.positive)
        }
        debateNegativeAnswerText.text = debateDetails.answers.negative.value
        debateNegativeAnswerButton.setOnClickListener {
            changeImagesInButtonsOnNegativeAnswer()
            controller.onVote(token, debateDetails.answers.negative)
        }
        debateNeutralAnswerText.text = debateDetails.answers.neutral.value
        debateNeutralAnswerButton.setOnClickListener {
            changeImagesInButtonsOnNeutralAnswer()
            controller.onVote(token, debateDetails.answers.neutral)
        }
    }

    override fun showLoader() = showLoader(debateDetailsCoordinator)

    override fun hideLoader() = hideLoader(debateDetailsCoordinator)

    override fun showDebateDetailsError(exception: Throwable) {
        showSnackbar(getString(R.string.debate_details_error))
    }

    override fun showVoteError(exception: Throwable) {
        showSnackbar(getString(R.string.debate_details_vote_error))
    }

    override fun resetImagesInButtons() {
        debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_positive_inactive))
        debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_negative_inactive))
        debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_neutral_inactive))
    }

    override fun showVoteSuccess() {
        showSnackbar(getString(R.string.debate_details_vote_success), Snackbar.LENGTH_SHORT)
    }

    private fun showSnackbar(text: String, length: Int = Snackbar.LENGTH_INDEFINITE) {
        Snackbar.make(debateDetailsCoordinator, text, length).show()
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

    private fun changeImagesInButtonsOnPositiveAnswer() {
        debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_positive_active))
        debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_negative_inactive))
        debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_neutral_inactive))
    }

    private fun changeImagesInButtonsOnNegativeAnswer() {
        debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_positive_inactive))
        debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_negative_active))
        debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_neutral_inactive))
    }

    private fun changeImagesInButtonsOnNeutralAnswer() {
        debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_positive_inactive))
        debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_negative_inactive))
        debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_neutral_active))
    }
}
