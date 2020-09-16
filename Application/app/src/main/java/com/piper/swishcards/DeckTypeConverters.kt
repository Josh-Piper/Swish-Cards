package com.piper.swishcards

import androidx.room.TypeConverter
import java.util.*

class DeckTypeConverters {
    @TypeConverter
    fun toUUI(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID): String? {
        return uuid?.toString()
    }

    @TypeConverter
    fun fromBoolean(boolean: Boolean): Int? {
        return when (boolean) {
            true -> 1
            else -> 0
        }
    }

    @TypeConverter
    fun toBoolean(boolean: Int): Boolean? {
        return when (boolean) {
            1 -> true
            else -> false
        }
    }
}