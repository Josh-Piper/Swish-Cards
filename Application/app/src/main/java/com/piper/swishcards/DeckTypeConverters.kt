package com.piper.swishcards

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.*

class DeckTypeConverters {
    @TypeConverter
    fun toUUI(uuid: String?): UUID? {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun fromUUID(uuid: UUID): String? {
        return uuid.toString()
    }

    @TypeConverter
    fun toCalender(time: Long): Calendar {
        var c = Calendar.getInstance()
        c.timeInMillis = time
        return c
    }

    @TypeConverter
    fun fromCalendar(calendar: Calendar): Long {
        return calendar.timeInMillis
    }
}