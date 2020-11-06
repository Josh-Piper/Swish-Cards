package com.piper.swishcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    //Declarations
    private val repository: DeckRepository
    private val hiddenRepo: CardRepository
    val allDecks: LiveData<List<Deck>>

    init {
        val decksDao = FlashCardDB.getDatabase(application, viewModelScope).DeckDAO() //Get database interface
        val cardsDao = FlashCardDB.getDatabase(application, viewModelScope).CardDAO()
        repository = DeckRepository.get(decksDao)
        hiddenRepo = CardRepository.get(cardsDao)
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
        hiddenRepo.deleteAllCardsFromParent(deck.id)
    }
}