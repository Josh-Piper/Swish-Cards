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
    private val hiddenCardRepository: CardRepository
    val allDecks: LiveData<List<Deck>>

    init {
        val decksDao = FlashCardDB.getDatabase(application, viewModelScope).DeckDAO()
        val cardsDao = FlashCardDB.getDatabase(application, viewModelScope).CardDAO()
        repository = DeckRepository.get(decksDao)
        hiddenCardRepository = CardRepository.get(cardsDao)
        allDecks = repository.getAllDecks()
    }

    fun selectCompletedDecks(): List<Deck>?  {
        return repository.getCompletedDecks()
    }

    //Delete Completed Decks
    fun deleteAllCompletedDecks() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAllCompletedDecks()
        //delete all FlashCards from Deck
        selectCompletedDecks()?.forEach { deck ->
            hiddenCardRepository.deleteAllCardsFromParent(deck.id)
        }
    }

    fun deleteAllDecks() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllDecks()
            hiddenCardRepository.deleteAllCards()
        }
    }






}