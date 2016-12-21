package pl.elpassion.project.dto

import pl.elpassion.project.Project

fun newProject(id: Long = 0, name: String = "name") = Project(id, name)
