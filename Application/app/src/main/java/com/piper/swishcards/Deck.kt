package com.piper.swishcards

import android.content.res.Resources
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "deck_table")
@Parcelize
data class Deck(
    @PrimaryKey @ColumnInfo(name = "id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "date") var date: String
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
    }
}