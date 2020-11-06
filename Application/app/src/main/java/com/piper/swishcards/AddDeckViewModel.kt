package com.piper.swishcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class AddDeckViewModel(application: Application) : AndroidViewModel(application) {

    //declarations
    val bad_words: MutableList<String> = mutableListOf()
    private lateinit var callBack: AddDeckCallBack

    var deck: Deck? = null

    fun init(passedDeck: Deck?, cl: AddDeckCallBack) {
        deck = passedDeck
        callBack = cl

        deck?.title?.let { callBack.setTitleFromModel(it) }
        deck?.date?.let { callBack.setDateFromModel(Deck.getStringFromCalendar(it)) }
    }

    fun getDeck(title: String, due: String): Deck {
        if (deck != null) {
            return deck as Deck
        } else {
            return Deck(
                title = title,
                date = Deck.getCalendarFromAU(due),
                completed = false
            )
        }
    }

    //Update the deck dependant on new information
    fun setDeck(title: String, date: String) {
        deck?.title = title
        deck?.date = Deck.getCalendarFromAU(date)
    }

    //Ensure that only valid data is passed through
    fun isValidationCorrect(title: String, due: String): Boolean {
        if ( (!checkExists(title) && (!checkExists(due) ))) {
            return true
        } else {
            callBack.setSnackBar("Invalid Input! check everything is correct")
            return false
        }
    }


    fun checkExists(str: String): Boolean { return str.isEmpty() }
}

