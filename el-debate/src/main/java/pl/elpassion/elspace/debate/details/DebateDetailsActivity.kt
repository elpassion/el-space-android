package pl.elpassion.elspace.debate.details

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ImageView
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

    private val animatorSet by lazy {
        AnimatorSet().apply {
            duration = 500
            playTogether(createObjectAnimator(debatePositiveAnswerImage), createObjectAnimator(debateNegativeAnswerImage), createObjectAnimator(debateNeutralAnswerImage))
        }
    }

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

    override fun onDestroy() {
        controller.onDestroy()
        super.onDestroy()
    }

    override fun showDebateDetails(debateDetails: DebateData) {
        debateDetails.run {
            debateTopic.text = topic
            debatePositiveAnswerText.text = answers.positive.value
            debateNegativeAnswerText.text = answers.negative.value
            debateNeutralAnswerText.text = answers.neutral.value
            when (lastAnswerId) {
                answers.positive.id -> changeImagesInButtonsOnAnswer { setPositiveActive() }
                answers.negative.id -> changeImagesInButtonsOnAnswer { setNegativeActive() }
                answers.neutral.id -> changeImagesInButtonsOnAnswer { setNeutralActive() }
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
        getLoaderForAnswer(answer).show()
    }

    private fun getLoaderForAnswer(answer: Answer) = when (answer) {
        is Positive -> debatePositiveAnswerLoader
        is Negative -> debateNegativeAnswerLoader
        is Neutral -> debateNeutralAnswerLoader
    }

    override fun hideVoteLoader() {
        debatePositiveAnswerLoader.hide()
        debateNeutralAnswerLoader.hide()
        debateNegativeAnswerLoader.hide()
    }

    override fun showVoteError(exception: Throwable) {
        showSnackbar(R.string.debate_details_vote_error)
    }

    override fun showVoteSuccess(answer: Answer) {
        showSnackbar(R.string.debate_details_vote_success, Snackbar.LENGTH_SHORT)
        when (answer) {
            is Positive -> changeImagesInButtonsOnAnswer { setPositiveActive() }
            is Negative -> changeImagesInButtonsOnAnswer { setNegativeActive() }
            is Neutral -> changeImagesInButtonsOnAnswer { setNeutralActive() }
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

    private fun createObjectAnimator(target: ImageView): ObjectAnimator = ObjectAnimator.ofFloat(target, "alpha", 0.5f, 1f)

    private fun changeImagesInButtonsOnAnswer(setActiveAnswer: () -> Unit) {
        animatorSet.run {
            removeAllListeners()
            addListener(getAnimatorListenerAdapter(setActiveAnswer))
            start()
        }
    }

    private fun getAnimatorListenerAdapter(setActiveAnswer: () -> Unit) = object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            setActiveAnswer()
        }
    }

    private fun setPositiveActive() {
        debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_positive_active))
        debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_negative_inactive))
        debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_neutral_inactive))
    }

    private fun setNegativeActive() {
        debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_positive_inactive))
        debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_negative_active))
        debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_neutral_inactive))
    }

    private fun setNeutralActive() {
        debatePositiveAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_positive_inactive))
        debateNegativeAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_negative_inactive))
        debateNeutralAnswerImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.hand_neutral_active))
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
