package pl.elpassion.elspace.hub.project.dto

import pl.elpassion.elspace.hub.project.Project

fun newProject(id: Long = 0, name: String = "name") = Project(id, name)
