package pl.elpassion.elspace.hub.project.last

import pl.elpassion.elspace.hub.project.Project

interface LastSelectedProjectRepository {
    fun getLastProject(): Project?
}
