package com.piper.swishcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class CardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CardRepository
    val allCards: LiveData<List<FlashCard>>

    init {
        val cardDao = FlashCardDB.getDatabase(application, viewModelScope).CardDAO() //gets Database interface
        repository = CardRepository.get(cardDao)
        allCards = repository.getAllCards()
    }

    fun sortCards(sortingMethod: SortCard, parentID: UUID) = viewModelScope.launch(Dispatchers.Main) {
        when (sortingMethod) {
            SortCard.ALL -> repository.sortCardBy(SortCard.ALL, UUID.randomUUID())
            SortCard.PARENT_ID -> repository.sortCardBy(SortCard.PARENT_ID, parentID)
        }
    }

    fun insertCard(card: FlashCard) = viewModelScope.launch(Dispatchers.IO) {
        repository.insertCard(card)
    }

    fun deleteCard(card: FlashCard) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteCard(card)
    }

    fun updateCard(card: FlashCard) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateCard(card)
    }

    fun deleteAllCardsFromParent(parentID: UUID) = viewModelScope.launch {
        repository.deleteAllCardsFromParent(parentID)
    }

    fun cardDeleteAll() = viewModelScope.launch (Dispatchers.IO) {
        repository.deleteAllCards()
    }


}