package com.piper.swishcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import java.util.*

class AddCardViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var callback: ValidateCallback
    var card: FlashCard? = null
    var deck: Deck? = null

    fun setCallback(cl: ValidateCallback) {
        callback = cl
    }

    //Return either the existing card to allow it to update. Or, create a new card.
    //This is located in a viewmodel to save the changes for orientation handling.
    fun getCards(inputQuestion: String, inputAnswer: String): FlashCard {
        card?.apply {
            question = inputQuestion
            answer = inputAnswer
        }

        if (card != null) return card as FlashCard
        return FlashCard(
            pid = deck?.id ?: UUID.randomUUID(),
            question = inputQuestion,
            answer = inputAnswer,
            type = CardType.SHORT_ANSWER,
            completed = false
        )
    }

    //TextWatcher is used for validating the size of the input. Thus, checking is not required.
    //Q. -> should the TextWatcher be moved into the ViewModel? however, would add a layer of abstraction.
    fun validateSucceeded(inputOne: String, inputTwo: String): Boolean {
        if (!(inputOne.isEmpty()) && !(inputTwo.isEmpty())) {
            return true
        }
        callback.setSnackBar("Incorrect Details! Input is Required")
        return false
    }
}