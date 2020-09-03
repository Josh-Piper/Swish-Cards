package com.piper.swishcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ComposeComparison : AppCompatActivity() {
    private lateinit var cardButton: Button
    private lateinit var nextButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose_comparison)

        cardButton = findViewById(R.id.flashCardBTN)
        nextButton = findViewById(R.id.nextCardBTN)

        val flashCards = FlashCards()
        cardButton.text = flashCards.currentFlashCards.question


        cardButton.setOnClickListener {
            cardButton.text = flashCards.currentFlashCards.answer
        }

        nextButton.setOnClickListener {
            flashCards.incrementQuestion()
            cardButton.text = flashCards.currentFlashCards.question
        }



    }
}

data class Question(val question: String, val answer: String) {
}

class FlashCards() {

    val flashCards = listOf(
        Question("How many Bananas should go in a Smoothie?", "3 Bananas"),
        Question("How many Eggs does it take to make an Omellete?", "8 Eggs"),
        Question("How do you say Hello in Japenese?", "Konichiwa"),
        Question("What is Korea's currency?", "Won")
    )

    var currentQuestion = 0

    val currentFlashCards
        get() = flashCards[currentQuestion]

    fun incrementQuestion() {
        if (currentQuestion + 1 >= flashCards.size) currentQuestion = 0 else currentQuestion++
    }
}
