package pl.elpassion.report.list

import java.util.*

class ReportFromApi(val createdAt: Date) {
    fun toReport(): Report {
        val year = Calendar.getInstance().apply { time = createdAt }.get(Calendar.YEAR)
        return Report(year = year, month = 6, day = 1, reportedHours = 4.0, projectId = 1, projectName = "Project", description = "description")
    }

}