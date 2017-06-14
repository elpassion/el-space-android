package pl.elpassion.elspace.debate.details

sealed class Answer(val id: Long, val value: String)

class Positive(id: Long, value: String) : Answer(id, value)

class Negative(id: Long, value: String) : Answer(id, value)

class Neutral(id: Long, value: String) : Answer(id, value)