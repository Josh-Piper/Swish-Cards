package com.piper.swishcards

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "deck_table")
@Parcelize
data class Deck(@PrimaryKey val title: String) : Parcelable {
}