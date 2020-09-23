package com.piper.swishcards

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "deck_table")
@Parcelize
data class Deck(
    @PrimaryKey @ColumnInfo(name = "id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "date") var date: Calendar,
    @ColumnInfo(name = "completed") var completed: Boolean
) : Parcelable {

    companion object {
        //Used for editing Deck purposes
        private fun addZeroToSingleDigit(i: Int): String {
            val str: String = i.toString()
            return if (str.length == 1) ("0$str") else str
        }
        //Format American date format to Aus.
        fun formatDateToAU(day: Int, month: Int, year: Int): String {
            //string placeholders causes crashes outside of Activities etc.
            return "${addZeroToSingleDigit(day)}-${addZeroToSingleDigit(month)}-${addZeroToSingleDigit(year)}"
        }

        fun getCalendarFromAU(dateAU: String): Calendar {
            val date = SimpleDateFormat( "dd-MM-yyyy", Locale.ENGLISH).parse(dateAU)
            val calender = Calendar.getInstance()
            calender.time = date
            return calender
        }

        fun getStringFromCalendar(calendar: Calendar): String {
            return SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(calendar.time) //Added Locale.English
        }
    }
}