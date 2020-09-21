package com.piper.swishcards

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FlashCard(var question: String, var answer: String, var type: CardType) : Parcelable {
}