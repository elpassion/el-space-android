package pl.elpassion.report.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.report_add_details_form_regular.view.*
import pl.elpassion.R
import pl.elpassion.common.inflate
import pl.elpassion.project.Project
import pl.elpassion.project.choose.ProjectChooseActivity
import pl.elpassion.project.last.LastSelectedProjectRepositoryProvider

class ReportAddDetailsRegularFragment : ReportAddDetails.View(), ReportAdd.View.Regular {
    override var controller: ReportAddDetailsRegularController? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflate(container!!, R.layout.report_add_details_form_regular)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = ReportAddDetailsRegularController(this, LastSelectedProjectRepositoryProvider.get())
        view?.reportAddProjectName?.setOnClickListener { controller?.onProjectClicked() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        controller = null
    }

    override fun showSelectedProject(project: Project) {
        view?.reportAddProjectName?.text = project.name
    }

    override fun openProjectChooser() {
        ProjectChooseActivity.startForResult(this, REQUEST_CODE)
    }

    override fun getDescription(): String = view?.reportAddDescription?.text.toString()

    override fun showEmptyDescriptionError() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            controller?.onSelectProject(ProjectChooseActivity.getProject(data!!))
        }
    }

    companion object {
        private val REQUEST_CODE = 10001
    }
}