package com.piper.swishcards

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView

class DeckRecyclerAdapter(context: Context) :
    RecyclerView.Adapter<DeckRecyclerAdapter.DeckViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var decks = emptyList<Deck>() //Cached

    override fun getItemCount() = decks.size

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val item = decks[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = inflater.inflate(R.layout.fragment_deck_item, parent, false) as View
        return DeckViewHolder(view)
    }

    internal fun setDecks(decks: List<Deck>) {
        this.decks = decks
        notifyDataSetChanged()
    }

    class DeckViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val title: TextView = v.findViewById(R.id.fragment_deck_item_title)

        fun bind(item: Deck) {
            title.text = item.title
        }
    }
}

