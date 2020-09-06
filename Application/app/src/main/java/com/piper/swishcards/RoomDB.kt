package com.piper.swishcards

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Dao
interface DeckDAO {
    @Query("SELECT * from deck_table ORDER BY title ASC")
    fun getDecksSortedByAlphaAsc(): LiveData<List<Deck>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(deck: Deck)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(deck: Deck)

    @Query("DELETE FROM deck_table")
    suspend fun deleteAll()
}

@Database(entities = [Deck::class], version = 1, exportSchema = false)
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
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}