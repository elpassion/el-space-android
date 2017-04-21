package pl.elpassion.elspace.debate.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.debate_details_activity.*
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import pl.elpassion.elspace.common.hideLoader
import pl.elpassion.elspace.common.showLoader
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class DebateDetailsActivity : AppCompatActivity(), DebateDetails.View {
    private val controller by lazy {
        DebateDetailsController(DebateDetails.ApiProvider.get(), this, SchedulersSupplier(Schedulers.io(), AndroidSchedulers.mainThread()))
    }

    companion object {
        private val debateAuthTokenCode = "debateAuthTokenKey"

        fun start(context: Context, debateToken: String) = context.startActivity(intent(context, debateToken))

        fun intent(context: Context, debateToken: String) =
                Intent(context, DebateDetailsActivity::class.java).apply {
                    putExtra(debateAuthTokenCode, debateToken)
                }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.debate_details_activity)
        controller.onCreate(debateAuthTokenCode)
    }

    override fun showDebateDetails(token: String, debateDetails: DebateData) {
        debateTopic.text = debateDetails.topic
        debatePositiveAnswer.text = debateDetails.answers.positive.value
        debatePositiveAnswer.setOnClickListener { controller.onVote(token, debateDetails.answers.positive) }
        debateNegativeAnswer.text = debateDetails.answers.negative.value
        debateNegativeAnswer.setOnClickListener { controller.onVote(token, debateDetails.answers.negative) }
        debateNeutralAnswer.text = debateDetails.answers.neutral.value
    }

    override fun showLoader() = showLoader(debateDetailsCoordinator)

    override fun hideLoader() = hideLoader(debateDetailsCoordinator)

    override fun showDebateDetailsError(exception: Throwable) {
        debateStatus.text = getString(R.string.debate_details_error)
    }

    override fun showVoteSuccess() {
        debateStatus.text = getString(R.string.debate_details_vote_success)
    }

    override fun showVoteError(exception: Throwable) = Unit
}
