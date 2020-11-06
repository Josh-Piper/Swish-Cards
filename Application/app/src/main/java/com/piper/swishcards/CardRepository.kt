package com.piper.swishcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class CardRepository(private val cardDAO: CardDAO) {

    //Declarations
    private val allCards = MediatorLiveData<List<FlashCard>>()
    private var oldSource: LiveData<List<FlashCard>> = MutableLiveData()

    fun getAllCards(): LiveData<List<FlashCard>> = allCards

    //MutableLiveData allows for merging of sources etc. Used for future proofing.
    private fun loadLiveData(newSource: LiveData<List<FlashCard>>) {
        allCards.removeSource(oldSource)
        oldSource = newSource
        allCards.addSource(newSource) { newCards ->
            allCards.value = newCards
        }
    }

    fun sortCardBy(sortingMethod: SortCard, parentID: UUID) {
        when (sortingMethod) {
            SortCard.ALL -> loadLiveData(cardDAO.getAllCards())
            SortCard.PARENT_ID -> loadLiveData(cardDAO.sortCardsByParentID(parentID))
        }
    }

    //insertCard()
    suspend fun insertCard(card: FlashCard) {
        cardDAO.insertCard(card)
    }

    //deleteCard()
    suspend fun deleteCard(card: FlashCard) {
        cardDAO.deleteCard(card)
    }

    //updateCard()
    suspend fun updateCard(card: FlashCard) {
        cardDAO.updateCard(card)
    }

    //deleteAllCardsFromParent()
    fun deleteAllCardsFromParent(parentID: UUID) {
        cardDAO.deleteAllCardsFromParent(parentID)
    }

    //cardDeleteAll()
    suspend fun deleteAllCards() {
        cardDAO.deleteAllCards()
    }
    companion object {
        //singleton approach
        private var cardRepo: CardRepository? = null

        fun get(cardDao: CardDAO): CardRepository {
            if (cardRepo == null) cardRepo = CardRepository(cardDao)
            return (cardRepo as CardRepository)
        }
    }
}