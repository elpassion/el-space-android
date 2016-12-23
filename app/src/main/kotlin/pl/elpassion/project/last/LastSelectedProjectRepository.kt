package pl.elpassion.project.last

import pl.elpassion.project.Project

interface LastSelectedProjectRepository {
    fun getLastProject(): Project?
}
