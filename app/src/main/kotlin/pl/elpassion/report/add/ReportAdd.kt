package pl.elpassion.report.add

import pl.elpassion.common.Provider
import pl.elpassion.project.common.Project
import rx.Observable

interface ReportAdd {
    interface View {
        fun showSelectedProject(project: Project)

        fun openProjectChooser()

        fun close()

        fun showError()
    }

    interface Api {
        fun addReport(): Observable<Unit>
    }

    object ApiProvider : Provider<Api>({
        throw NotImplementedError()
    })
}