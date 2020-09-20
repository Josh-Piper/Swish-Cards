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
        repository = DeckRepository.get(decksDao)
        allDecks = repository.getAllDecks() //should this be observing
    }

    fun sortBy(sortMethod: Sort) = viewModelScope.launch(Dispatchers.Main) {
        when (sortMethod) {
            Sort.ALPHA_ASC -> repository.sortBy(Sort.ALPHA_ASC)
            Sort.ALPHA_DES -> repository.sortBy(Sort.ALPHA_DES)
            Sort.NON_COM -> repository.sortBy(Sort.NON_COM)
            Sort.DUE_DATE -> repository.sortBy(Sort.DUE_DATE)
        }
    }

    fun insert(deck: Deck) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(deck)
    }

    fun update(deck: Deck) = viewModelScope.launch(Dispatchers.IO) {
        repository.upate(deck)
    }

    fun delete(deck: Deck) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(deck)
    }

    fun deleteAllDecks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllDecks()
        }
    }
}