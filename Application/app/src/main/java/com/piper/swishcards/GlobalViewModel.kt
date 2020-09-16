package com.piper.swishcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GlobalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DeckRepository
    val allDecks: LiveData<List<Deck>>

    init {
        val decksDao = FlashCardDB.getDatabase(application, viewModelScope).DeckDAO()
        repository = DeckRepository(deckDao = decksDao)
        allDecks = repository.allDecks
    }

    fun insert(deck: Deck) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(deck)
    }

    fun update(deck: Deck) = viewModelScope.launch(Dispatchers.IO) {
        repository.upate(deck)
    }

    fun deleteAllDecks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllDecks()
        }
    }
}