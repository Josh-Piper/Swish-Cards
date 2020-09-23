package com.piper.swishcards

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

class DeckRepository(private val deckDao: DeckDAO) {

    private var allDecks = MediatorLiveData<List<Deck>>() //MutableLiveData<List<Deck>>() //instantiate object
    private var oldSource: LiveData<List<Deck>> = MutableLiveData()

    fun getAllDecks(): LiveData<List<Deck>> = allDecks //Repository handles livedata transmission. ViewModel references the actual Data. When allDecks data is changed, should be LiveData and also sortable


    //changes source of livedata.
    private fun loadLiveData(newSource: LiveData<List<Deck>>){
        //issue here as it will listen to all the sources and compile them together. Only require a single source to be switched out.
        allDecks.removeSource(oldSource)
        oldSource = newSource
        allDecks.addSource(newSource) { newDeck ->
            allDecks.value = newDeck
        }
    }

    fun deleteAllCompletedDecks() {
        deckDao.deleteAllCompletedDecks()
    }

    //Changes datasource of livedata
    fun sortBy(sortingMethod: Sort) {

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
    companion object {
        private var deckRepo: DeckRepository? = null

        fun get(decksDAO: DeckDAO): DeckRepository {
            if (deckRepo == null) deckRepo = DeckRepository(decksDAO)
            return (deckRepo as DeckRepository)
        }
    }
}