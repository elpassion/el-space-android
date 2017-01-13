package pl.elpassion.space.pacman.json

import java.io.File

fun stringFromFile(fileName: String) = fileFromResources(fileName).readLines().joinToString("\n")

fun fileFromResources(name: String) = File((File::class as Any).javaClass.classLoader.getResource(name).file)