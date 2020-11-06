package com.piper.swishcards

//Used in FlashCardsOverview, interraction between AddCard activity and FlashCardsOverview
interface AddCardCallback {
    fun onUpdateCard(card: FlashCard)
}