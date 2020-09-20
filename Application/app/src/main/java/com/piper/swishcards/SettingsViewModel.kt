package com.piper.swishcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    //lightModeDetails
    var darkMode = false //if made into a login system. This information could be stored in a database. Currently stored in cached?

    //connect to the same Database/Repository as globalViewModel
    private val repository: DeckRepository
    val allDecks: LiveData<List<Deck>>

    init {
        val decksDao = FlashCardDB.getDatabase(application, viewModelScope).DeckDAO()
        repository = DeckRepository.get(decksDao)
        allDecks = repository.getAllDecks() //should this be observing
    }

    //Delete Completed Decks
    fun deleteAllCompletedDecks() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllCompletedDecks()
    }






}