package com.piper.swishcards

import androidx.lifecycle.LiveData
import java.util.*

class DeckRepository(private val deckDao: DeckDAO) {

    var allDecks: LiveData<List<Deck>> = deckDao.getDecksSortedByAlphaAsc()

    fun sortBy(value: Sort) {
        when (value) {
            Sort.ALPHA_ASC -> allDecks = deckDao.getDecksSortedByAlphaAsc()
            Sort.ALPHA_DES -> allDecks = deckDao.getDecksSortedByAlphaDesc()
            Sort.NON_COM -> allDecks = deckDao.getDecksSortedByNonCompleted()
            Sort.DUE_DATE -> allDecks = deckDao.getDecksSortedByDueDate()
        }
    }

    suspend fun insert(deck: Deck) {
        deckDao.insert(deck)
    }

    suspend fun upate(deck: Deck) {
        deckDao.update(deck)
    }

    suspend fun delete(deck: Deck) {
        deckDao.delete(deck)
    }

    suspend fun deleteAllDecks() {
        deckDao.deleteAll()
    }

}