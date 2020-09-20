package com.piper.swishcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    var lightMode = false

    //connect to the same Database/Repository as globalViewModel
    private val repository: DeckRepository
    val allDecks: LiveData<List<Deck>>

    init {
        val decksDao = FlashCardDB.getDatabase(application, viewModelScope).DeckDAO()
        repository = DeckRepository.get(decksDao)
        allDecks = repository.getAllDecks() //should this be observing
    }


    //lightModeDetails


    //Delete Completed Decks

}