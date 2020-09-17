package com.piper.swishcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class DeckRepository(private val deckDao: DeckDAO) {

    private var allDecks = MediatorLiveData<List<Deck>>() //MutableLiveData<List<Deck>>() //instantiate object

    fun getAllDecks(): LiveData<List<Deck>> = allDecks //Repository handles livedata transmission. ViewModel references the actual Data. When allDecks data is changed, should be LiveData and also sortable


    //changes source of livedata.
    private fun loadLiveData(sort: LiveData<List<Deck>>){
        allDecks.addSource(sort) {
            allDecks.value = it
        }
    }

    //Changes datasource of livedata
    suspend fun sortBy(sortingMethod: Sort) {

        when (sortingMethod) {
            Sort.ALPHA_ASC -> loadLiveData(deckDao.getDecksSortedByAlphaAsc())
            Sort.ALPHA_DES -> loadLiveData(deckDao.getDecksSortedByAlphaDesc())
            Sort.NON_COM -> loadLiveData( deckDao.getDecksSortedByNonCompleted())
            Sort.DUE_DATE -> loadLiveData(deckDao.getDecksSortedByDueDate())
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