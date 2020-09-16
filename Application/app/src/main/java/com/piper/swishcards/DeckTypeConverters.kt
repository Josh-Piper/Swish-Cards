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
}