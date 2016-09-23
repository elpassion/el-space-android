package pl.elpassion.project.dto

import pl.elpassion.project.common.Project

fun newProject(id: String = "id", name: String = "name") = Project(id, name)
