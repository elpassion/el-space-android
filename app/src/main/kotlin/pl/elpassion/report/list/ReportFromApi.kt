package pl.elpassion.report.list

import pl.elpassion.common.monthValue
import pl.elpassion.common.yearValue
import java.util.*

class ReportFromApi(val createdAt: Date) {

    fun toReport(): Report {
        return Report(year = createdAt.yearValue(), month = createdAt.monthValue(), day = 1, reportedHours = 4.0, projectId = 1, projectName = "Project", description = "description")
    }

}