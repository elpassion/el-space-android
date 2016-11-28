package pl.elpassion.report.list

import pl.elpassion.project.Project
import pl.elpassion.report.Report

class ReportFromApi(val performedAt: String, val value: Double?, val projectId: Long?, val comment: String?) {

    fun toReport(projects: List<Project>): Report {
        val date = performedAt.split("-")
        return Report(
                year = date[0].toInt(),
                month = date[1].toInt(),
                day = date[2].toInt(),
                reportedHours = value ?: 0.0,
                projectId = projectId ?: -1,
                projectName = projects.firstOrNull { it.id == projectId.toString() }?.name ?: "Unknown",
                description = comment ?: "")
    }

}