package pl.elpassion.elspace.dabate.details

import pl.elpassion.elspace.debate.details.*

fun createDebateData(debateTopic: String = "topic", answers: Answers = createAnswers(), lastAnswerId: Long? = -1)
        = DebateData(debateTopic, answers, lastAnswerId)

fun createAnswers(positiveAnswer: Positive = createPositiveAnswer(),
                  negativeAnswer: Negative = createNegativeAnswer(),
                  neutralAnswer: Neutral = createNeutralAnswer())
        = Answers(positiveAnswer, negativeAnswer, neutralAnswer)

fun createPositiveAnswer(answerId: Long = 1, answerLabel: String = "answerPositive")
        = Positive(answerId, answerLabel)

fun createNegativeAnswer(answerId: Long = 2, answerLabel: String = "answerNegative")
        = Negative(answerId, answerLabel)

fun createNeutralAnswer(answerId: Long = 3, answerLabel: String = "answerNeutral")
        = Neutral(answerId, answerLabel)