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
    @Query("SELECT * from deck_table ORDER BY title ASC")
    fun getDecksSortedByAlphaAsc(): LiveData<List<Deck>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(deck: Deck)


    //new Update query (based on UUID) for SQLite => "UPDATE deck_table SET title = deck.title, date = deck.date WHERE uuid == deck.uuid"
    @Update(onConflict = OnConflictStrategy.REPLACE)//@Query("UPDATE deck_table SET title=(:title), date = (:date) WHERE id = (:id)")
    suspend fun update(deck: Deck)

    @Query("DELETE FROM deck_table")
    suspend fun deleteAll()
}

@Database(entities = [Deck::class], version = 3, exportSchema = false)
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