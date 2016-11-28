package pl.elpassion.report

data class Report(
        val id : Long,
        val year: Int,
        val month: Int,
        val day: Int,
        val reportedHours: Double,
        val projectName: String,
        val projectId: Long,
        val description: String)