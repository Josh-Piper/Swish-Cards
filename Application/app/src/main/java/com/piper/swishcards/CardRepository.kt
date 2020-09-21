package com.piper.swishcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class CardRepository(private val deckDAO: DeckDAO) {

    private val allCards = MediatorLiveData<List<FlashCard>>()
    private var oldSource: LiveData<List<FlashCard>> = MutableLiveData()

    fun getAllCards(): LiveData<List<FlashCard>> = allCards

    private fun loadLiveData(newSource: LiveData<List<FlashCard>>) {
        allCards.removeSource(oldSource)
        oldSource = newSource
        allCards.addSource(newSource) { newCard ->
            allCards.value = newCard
        }
    }

    fun sortCardBy(sortingMethod: SortCard, parentID: UUID) {
        when (sortingMethod) {
            SortCard.ALL -> loadLiveData(deckDAO.getAllCards())
            SortCard.PARENT_ID -> loadLiveData(deckDAO.sortCardsByParentID(parentID))
        }
    }

    //insertCard()
    suspend fun insertCard(card: FlashCard) {
        deckDAO.insertCard(card)
    }

    //deleteCard()
    suspend fun deleteCard(card: FlashCard) {
        deckDAO.deleteCard(card)
    }

    //updateCard()
    suspend fun updateCard(card: FlashCard) {
        deckDAO.deleteCard(card)
    }

    //deleteAllCardsFromParent()
    suspend fun deleteAllCardsFromParent(parentID: UUID) {
        deckDAO.deleteAllCardsFromParent(parentID)
    }

    //cardDeleteAll()
    suspend fun cardDeleteAll() {
        deckDAO.cardDeleteAll()
    }
    companion object {
        private var cardRepo: CardRepository? = null

        fun get(decksDAO: DeckDAO): CardRepository {
            if (cardRepo == null) cardRepo = CardRepository(decksDAO)
            return (cardRepo as CardRepository)
        }
    }
}