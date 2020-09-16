package com.piper.swishcards

import androidx.lifecycle.LiveData
import java.util.*

class DeckRepository(private val deckDao: DeckDAO) {

    val allDecks: LiveData<List<Deck>> = deckDao.getDecksSortedByAlphaAsc()

    suspend fun insert(deck: Deck) {
        deckDao.insert(deck)
    }

    suspend fun upate(deck: Deck) {
        deckDao.update(deck)
    }

    suspend fun deleteAllDecks() {
        deckDao.deleteAll()
    }

}