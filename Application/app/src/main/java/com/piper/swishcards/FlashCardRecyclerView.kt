package com.piper.swishcards

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlashCardRecyclerView(context: Context): RecyclerView.Adapter<FlashCardRecyclerView.FlashCardHolder>() {

    var flashCards = emptyList<FlashCard>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int = flashCards.size

    override fun onBindViewHolder(holder: FlashCardHolder, position: Int) {
        val item = flashCards[position]
        holder.bind(item)

        holder.apply {
            layout.setOnLongClickListener {
                //set startActivity to Update a Card's information here.
                //Need to add a OnResult as well...
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashCardHolder {
        val view = inflater.inflate(R.layout.fragment_flash_card_item, parent, false)
        return FlashCardHolder(view)
    }

    fun setCards(cards: List<FlashCard>) {
        flashCards = cards
    }

    class FlashCardHolder(v: View) : RecyclerView.ViewHolder(v) {
        val layout: LinearLayout = v.findViewById(R.id.linear_layout_base)
        val checkbox: CheckBox = v.findViewById(R.id.card_checkbox)
        private val cardTitle: TextView = v.findViewById(R.id.card_title)

        fun bind(item: FlashCard) {
            val title = if (item.question.length > 10) item.question.substring(0, 10) + ".." else item.question
            checkbox.isChecked = item.completed
            cardTitle.setText(title)
        }
    }
}