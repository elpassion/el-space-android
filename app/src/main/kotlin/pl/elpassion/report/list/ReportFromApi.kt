package pl.elpassion.report.list

import pl.elpassion.common.dayValue
import pl.elpassion.common.monthValue
import pl.elpassion.common.yearValue
import java.util.*

class ReportFromApi(val createdAt: Date, val value : Double, val projectId: Long, val comment: String) {

    fun toReport(): Report {
        return Report(
                year = createdAt.yearValue(),
                month = createdAt.monthValue(),
                day = createdAt.dayValue(),
                reportedHours = value,
                projectId = projectId,
                projectName = "Project",
                description = comment)
    }

}