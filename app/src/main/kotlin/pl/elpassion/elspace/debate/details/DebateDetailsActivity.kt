package pl.elpassion.elspace.debate.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import pl.elpassion.R
import pl.elpassion.elspace.common.SchedulersSupplier
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import kotlinx.android.synthetic.main.debate_details_activity.*

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

    override fun showDebateDetails(debateDetails: DebateData) {
        debateDetailsTopic.text = debateDetails.topic
        debateDetailsPositiveAnswer.text = debateDetails.answers.positive.value
    }

    override fun showLoader() = Unit

    override fun hideLoader() = Unit

    override fun showDebateDetailsError(exception: Throwable) = Unit

    override fun showVoteSuccess() = Unit

    override fun showVoteError(exception: Throwable) = Unit
}
