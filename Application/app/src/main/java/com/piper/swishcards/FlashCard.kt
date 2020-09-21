package com.piper.swishcards

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Entity(tableName = "card_table")
@Parcelize
data class FlashCard(
    @PrimaryKey @ColumnInfo(name = "id") val id: UUID = UUID.randomUUID(),
    @ColumnInfo(name = "parent_id") var pid: UUID,
    @ColumnInfo(name = "question") var question: String,
    @ColumnInfo(name = "answer") var answer: String,
    @ColumnInfo(name = "type") var type: CardType)
    : Parcelable {
}