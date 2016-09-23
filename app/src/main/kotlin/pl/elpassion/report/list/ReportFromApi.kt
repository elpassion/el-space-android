package pl.elpassion.report.list

import pl.elpassion.common.dayValue
import pl.elpassion.common.monthValue
import pl.elpassion.common.yearValue
import pl.elpassion.project.common.Project
import java.util.*

class ReportFromApi(val createdAt: Date, val value: Double, val projectId: Long, val comment: String) {

    fun toReport(projects: List<Project>): Report {
        return Report(
                year = createdAt.yearValue(),
                month = createdAt.monthValue(),
                day = createdAt.dayValue(),
                reportedHours = value,
                projectId = projectId,
                projectName = projects.first { it.id.equals(projectId.toString()) }.name,
                description = comment)
    }

}