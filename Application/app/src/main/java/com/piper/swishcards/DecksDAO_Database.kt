package com.piper.swishcards

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.CoroutineScope
import java.util.*

//The following shows the database queries for deck_table
//
//
////////////////////
@Dao
interface DeckDAO {


    //Sorting for deck_table
    @Query("SELECT * from deck_table ORDER BY title ASC")
    fun getDecksSortedByAlphaAsc(): LiveData<List<Deck>>


    @Query("SELECT * from deck_table ORDER BY title DESC")
    fun getDecksSortedByAlphaDesc(): LiveData<List<Deck>>

    @Query("SELECT * from deck_table WHERE completed = (:completed) ORDER BY title ASC")
    fun getDecksSortedByNonCompleted(completed: Boolean = false): LiveData<List<Deck>>

    @Query("SELECT * from deck_table ORDER BY date ASC")
    fun getDecksSortedByDueDate(): LiveData<List<Deck>>

    //Delete all Completed decks from deck_table
    //and delete all Decks from deck_table
    @Query("DELETE FROM deck_table WHERE completed = 1")
    fun deleteAllCompletedDecks()

    @Query("DELETE FROM deck_table")
    suspend fun deleteAll()

    //Regular queries
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(deck: Deck)


}


//The following lists the different methods used for card_table
//
//
//////////////////////////////////
@Dao
interface CardDAO {

    @Query("DELETE FROM card_table")
    suspend fun deleteAllCards()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCard(card: FlashCard)

    @Delete
    suspend fun deleteCard(card: FlashCard)

    @Insert
    suspend fun insertCard(card: FlashCard)

    @Query("SELECT * FROM card_table WHERE parent_id = (:pid) ORDER BY question ASC")
    fun sortCardsByParentID(pid: UUID): LiveData<List<FlashCard>>

    @Query("SELECT * FROM card_table")
    fun getAllCardsFromParent(): LiveData<List<FlashCard>>

    @Query("SELECT * FROM card_table")
    fun getAllCards(): LiveData<List<FlashCard>>


    @Query("DELETE FROM card_table where parent_id=:pid")
    fun deleteAllCardsFromParent(pid: UUID)
}

//Database
//
///////////////////////
@Database(entities = [Deck::class, FlashCard::class], version = 9, exportSchema = false)
@TypeConverters(DeckTypeConverters::class)
abstract class FlashCardDB : RoomDatabase() {

    abstract fun DeckDAO(): DeckDAO
    abstract fun CardDAO(): CardDAO

    companion object {
        //Singleton design pattern to ensure only one database is created. Always reference the same Database which in turn, references the different tables (entities)
        private var INSTANCE: FlashCardDB? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FlashCardDB {
            val tmpInstance = INSTANCE

            if (tmpInstance != null) return tmpInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FlashCardDB::class.java,
                    "flash_cards_database"
                ).build() //.fallbackToDestructiveMigration() will causes users to loose data during migrations (use only for testing purposes)
                INSTANCE = instance
                return instance
            }
        }
    }
}