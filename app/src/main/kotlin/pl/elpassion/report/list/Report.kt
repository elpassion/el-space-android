package pl.elpassion.report.list

data class Report(
        val year: Int,
        val month: Int,
        val day: Int,
        val reportedHours: Double,
        val projectName: String,
        val projectId: Long,
        val description: String)