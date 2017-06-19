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
                changeImagesInButtonsOnAnswer { setPositiveActive() }
                controller.onVote(token, answers.positive)
            }
            debateNegativeAnswerButton.setOnClickListener {
                changeImagesInButtonsOnAnswer { setNegativeActive() }
                controller.onVote(token, answers.negative)
            }
            debateNeutralAnswerButton.setOnClickListener {
                changeImagesInButtonsOnAnswer { setNeutralActive() }
                controller.onVote(token, answers.neutral)
            }
        }
    }

    override fun showLoader() = showLoader(debateDetailsCoordinator)

    override fun hideLoader() = hideLoader(debateDetailsCoordinator)

    override fun showDebateDetailsError(exception: Throwable) {
        showSnackbar(getString(R.string.debate_details_error))
    }

    override fun showVoteLoader(answer: Answer) {
        val loaderRoot = when (answer) {
            is Positive -> {
                debatePositiveAnswerLoader.apply {
                    setLoaderColor(R.color.blueDebatePositive)
                }
            }
            is Negative -> {
                debateNegativeAnswerLoader.apply {
                    setLoaderColor(R.color.redDebateNegative)
                }
            }
            is Neutral -> {
                debateNeutralAnswerLoader.apply {
                    setLoaderColor(R.color.greyDebateNeutral)
                }
            }
        }
        loaderRoot.showLoader()
    }

    override fun hideVoteLoader() {
        debatePositiveAnswerLoader.hideLoader()
        debateNegativeAnswerLoader.hideLoader()
        debateNeutralAnswerLoader.hideLoader()
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

    private fun createObjectAnimator(target: ImageView): ObjectAnimator = ObjectAnimator.ofFloat(target, "alpha", 0.5f, 1f)

    private fun changeImagesInButtonsOnAnswer(setActiveAnswer: () -> Unit) {
        animatorSet.apply {
            removeAllListeners()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator?) {
                    super.onAnimationStart(animation)
                    setActiveAnswer()
                }
            })
        }.start()
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
}
