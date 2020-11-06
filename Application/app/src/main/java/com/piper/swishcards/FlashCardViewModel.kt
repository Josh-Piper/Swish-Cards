package com.piper.swishcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class FlashCardViewModel(application: Application) : AndroidViewModel(application)  {

    private lateinit var flashCards: List<FlashCard>
    private var currentCard = 0
    private lateinit var callback: CardReviewalCallbacks
    var isCompletedReview: Boolean = false
    var correctAnswers = 0

    private var maxCards: Int = 0

    fun setDependencies(cards: List<FlashCard>, cl: CardReviewalCallbacks) {
        flashCards = cards
        callback = cl
        maxCards = (flashCards.size - 1)
        callback.setQuestionText(flashCards[currentCard].question)
        callback.setProgressMax(maxCards)
    }

    //Logic
    fun incrementCard(ans: String) {
        if (!(currentCard + 1 > maxCards)) {
            checkAnswer(ans)
            currentCard++
            callback.setQuestionText(flashCards[currentCard].question)
            callback.increaseProgressBar()
        }
        if (currentCard == maxCards) { callback.setNextButtonText("Finish"); isCompletedReview = true}
        if (currentCard == maxCards && isCompletedReview == true) setFinishedReview()
    }

    fun checkAnswer(usersInput: String) = if (isCorrect(usersInput)) correctAnswers++ else correctAnswers

    fun isCorrect(usersInput: String): Boolean {
        return flashCards[currentCard].answer.toLowerCase().trim() == usersInput.toLowerCase().trim()
    }

    fun setFinishedReview() {
        var message: String = "Good Job! Getting Smarter Already\n"
        val percentage: Float = correctAnswers.toFloat() / maxCards.toFloat() * 100.0f
        message += "You Achieved ${correctAnswers} out of ${maxCards} | ${percentage}% \n"
        message += when (percentage) {
            100f -> "The Next Eintein Is On the Way!!!"
            in 80f..95f -> "Emaculate Job, Are You Bill Gates!"
            in 61f..75f -> "Spended, Keep It At"
            in 50f..60.9f -> "Barely Passing, You Got This"
            else -> "Slow and Steady Wins the Race!"
        }
        callback.finishedReviewal(message)
    }
}