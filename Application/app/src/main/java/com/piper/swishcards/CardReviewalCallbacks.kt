package com.piper.swishcards

interface CardReviewalCallbacks {

    fun increaseProgressBar()
    fun setQuestionText(question: String)
    fun setProgressMax(max: Int)
    fun setNextButtonText(text: String)
    fun finishedReviewal(message: String)
}