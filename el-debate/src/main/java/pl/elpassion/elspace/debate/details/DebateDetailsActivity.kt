package pl.elpassion.elspace.debate.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
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
        debateTopic.text = debateDetails.topic
        debatePositiveAnswerText.text = debateDetails.answers.positive.value
        debatePositiveAnswerButton.setOnClickListener {
            controller.onVote(token, debateDetails.answers.positive)
            highlightPositiveAnswer()
        }
        debateNegativeAnswerText.text = debateDetails.answers.negative.value
        debateNegativeAnswerButton.setOnClickListener {
            controller.onVote(token, debateDetails.answers.negative)
            highlightNegativeAnswer()
        }
        debateNeutralAnswerText.text = debateDetails.answers.neutral.value
        debateNeutralAnswerButton.setOnClickListener {
            controller.onVote(token, debateDetails.answers.neutral)
            highlightNeutralAnswer()
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

    private fun showSnackbar(text: String) {
        Snackbar.make(debateDetailsCoordinator, text, Snackbar.LENGTH_INDEFINITE).show()
    }

    override fun showVoteSuccess() {
        Snackbar.make(debateDetailsCoordinator, getString(R.string.debate_details_vote_success), Snackbar.LENGTH_SHORT).show()
    }

    override fun openCommentScreen() {
        DebateCommentActivity.start(this)
    }

    private fun highlightPositiveAnswer() {
        debatePositiveAnswerImage.setBackgroundResource(R.drawable.hand_background_blue_pressed)
        debateNegativeAnswerImage.setBackgroundResource(R.drawable.hand_background)
        debateNeutralAnswerImage.setBackgroundResource(R.drawable.hand_background)
    }

    private fun highlightNegativeAnswer() {
        debatePositiveAnswerImage.setBackgroundResource(R.drawable.hand_background)
        debateNegativeAnswerImage.setBackgroundResource(R.drawable.hand_background_red_pressed)
        debateNeutralAnswerImage.setBackgroundResource(R.drawable.hand_background)
    }

    private fun highlightNeutralAnswer() {
        debatePositiveAnswerImage.setBackgroundResource(R.drawable.hand_background)
        debateNegativeAnswerImage.setBackgroundResource(R.drawable.hand_background)
        debateNeutralAnswerImage.setBackgroundResource(R.drawable.hand_background_grey_pressed)
    }
}
