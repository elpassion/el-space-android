package pl.elpassion.project.dto

import pl.elpassion.project.Project

fun newProject(id: String = "id", name: String = "name") = Project(id, name)
