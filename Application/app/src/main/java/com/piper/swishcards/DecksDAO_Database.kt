package com.piper.swishcards

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Dao
interface DeckDAO {

    //All Deck interactions for deck_table
    //Sorting
    @Query("SELECT * from deck_table ORDER BY title ASC")
    fun getDecksSortedByAlphaAsc(): LiveData<List<Deck>>


    @Query("SELECT * from deck_table ORDER BY title DESC")
    fun getDecksSortedByAlphaDesc(): LiveData<List<Deck>>

    @Query("SELECT * from deck_table WHERE completed = (:completed) ORDER BY title ASC")
    fun getDecksSortedByNonCompleted(completed: Boolean = false): LiveData<List<Deck>>

    @Query("SELECT * from deck_table ORDER BY date ASC")
    fun getDecksSortedByDueDate(): LiveData<List<Deck>>

    @Query("DELETE FROM deck_table WHERE completed = 1")
    fun deleteAllCompletedDecks()

    //Modifying
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(deck: Deck)

    @Delete
    suspend fun delete(deck: Deck)

    //new Update query (based on UUID) for SQLite => "UPDATE deck_table SET title = deck.title, date = deck.date WHERE uuid == deck.uuid"
    @Update(onConflict = OnConflictStrategy.REPLACE)//@Query("UPDATE deck_table SET title=(:title), date = (:date) WHERE id = (:id)")
    suspend fun update(deck: Deck)

    @Query("DELETE FROM deck_table")
    suspend fun deleteAll()


    //All Card interactions for card_table

    @Query("DELETE FROM card_table")
    suspend fun cardDeleteAll()

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateCard(card: FlashCard)

    @Delete
    suspend fun deleteCard(card: FlashCard)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCard(card: FlashCard)

    @Query("SELECT * FROM card_table WHERE parent_id = (:pid) ORDER BY question ASC")
    fun sortCardsByParentID(pid: UUID): LiveData<List<FlashCard>>

    @Query("SELECT * FROM card_table")
    fun getAllCards(): LiveData<List<FlashCard>>


    @Query("DELETE FROM card_table where parent_id=:pid")
    fun deleteAllCardsFromParent(pid: UUID) //return nothing as it is deleting

}

@Database(entities = [Deck::class, FlashCard::class], version = 8, exportSchema = false)
@TypeConverters(DeckTypeConverters::class)
abstract class FlashCardDB : RoomDatabase() {

    abstract fun DeckDAO(): DeckDAO

    companion object {
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