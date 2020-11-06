package com.piper.swishcards

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FlashCardRecyclerView(context: Context, callback: AddCardCallback): RecyclerView.Adapter<FlashCardRecyclerView.FlashCardHolder>() {

    val mContext = context //FlashCardsOverview context passed through. Allows the activity to deal with all data to pass to viewmodel
    var flashCards = emptyList<FlashCard>()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    val callback: AddCardCallback = callback

    override fun getItemCount(): Int = flashCards.size

    override fun onBindViewHolder(holder: FlashCardHolder, position: Int) {
        val item = flashCards[position]
        holder.bind(item)
        holder.checkbox.isChecked = item.completed

        holder.apply {
            layout.setOnLongClickListener {
                //set startActivity to Update a Card's information here.

                val intent = Intent(this.layout.context, AddCardActivity::class.java).apply {
                    putExtra(CardPassedItemKey, item)
                }
                //cast to MainActivity since it is observing any changes to Decks. Therefore, it will deal with change.
                (mContext as FlashCardsOverview).startActivityForResult(intent, AddCardkActivityStartForResult)
                true
            }

            checkbox.setOnCheckedChangeListener { checkBox: CompoundButton, _isChecked: Boolean ->
                item.completed = _isChecked
                callback.onUpdateCard(item)
                checkBox.isChecked = item.completed
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashCardHolder {
        val view = inflater.inflate(R.layout.fragment_flash_card_item, parent, false)
        return FlashCardHolder(view)
    }

    fun setCards(cards: List<FlashCard>) {
        this.flashCards = cards
        notifyDataSetChanged()
        Log.i(MainActivity.GlobalLoggingName, "setCards called")
    }

    companion object {
        val AddCardkActivityStartForResult = 2020
        val CardPassedItemKey = "card_passed_from_deck_recycler_view"
    }

    class FlashCardHolder(v: View) : RecyclerView.ViewHolder(v) {
        val layout: LinearLayout = v.findViewById(R.id.linear_layout_base)
        val checkbox: CheckBox = v.findViewById(R.id.card_checkbox)
        private val cardTitle: TextView = v.findViewById(R.id.card_title)

        fun bind(item: FlashCard) {
            Log.i(MainActivity.GlobalLoggingName, "binding called")
            val title = if (item.question.length > 10) item.question.substring(0, 10) + ".." else item.question
            cardTitle.setText(title)
        }
    }
}