package com.piper.swishcards

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class FlashCardRecyclerView: RecyclerView.Adapter<FlashCardRecyclerView.FlashCardHolder>() {

    var flashCards = emptyList<FlashCard>()

    override fun getItemCount(): Int = flashCards.size

    override fun onBindViewHolder(holder: FlashCardHolder, position: Int) {
        val item = flashCards[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashCardHolder {
        TODO("Not yet implemented")
    }

    fun setCards(cards: List<FlashCard>) {
        flashCards = cards
    }

    class FlashCardHolder(v: View) : RecyclerView.ViewHolder(v) {

        fun bind(item: FlashCard) {
            //do nothing
        }
    }



}