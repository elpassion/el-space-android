package pl.elpassion.elspace.dabate.details

import pl.elpassion.elspace.debate.details.Answer
import pl.elpassion.elspace.debate.details.Answers
import pl.elpassion.elspace.debate.details.DebateData

fun createDebateData(debateTopic: String = "topic", answers: Answers = createAnswers(), lastAnswerId: Long? = -1)
        = DebateData(debateTopic, answers, lastAnswerId)

fun createAnswers(positiveAnswer: Answer = createAnswer(1, "answerPositive"),
                          negativeAnswer: Answer = createAnswer(2, "answerNegative"),
                          neutralAnswer: Answer = createAnswer(3, "answerNeutral"))
        = Answers(positiveAnswer, negativeAnswer, neutralAnswer)

fun createAnswer(answerId: Long = 1, answerLabel: String = "answer")
        = Answer(answerId, answerLabel)
