package com.piper.swishcards

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.RecyclerView



//ViewAdapter
class DeckRecyclerAdapter(context: Context) :
    RecyclerView.Adapter<DeckRecyclerAdapter.DeckViewHolder>() {

    val mContext = context //MainActivity context passed through. Allows MainActivity to run startActivityForResult
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var decks = emptyList<Deck>() //Cached deck for passing livedata

    override fun getItemCount() = decks.size

    override fun onBindViewHolder(holder: DeckViewHolder, position: Int) {
        val item = decks[position]

        Log.i("wow", "item is ${item.title} and is: ${item.completed}")

        holder.checkBox.isChecked = item.completed

        Log.i("wow", "${holder.checkBox.isChecked} and ${item.completed}")
        holder.bind(item)
        //can holder.apply everything
        //manages individual (set here to allow prioritising different decks, i.e. dates)
        holder.layout.setOnLongClickListener {view ->
            val intent = Intent(view.context, AddDeckActivity::class.java).apply {
                putExtra(DeckPassedItemKey, item)
            }
            //cast to MainActivity since it is observing any changes to Decks. Therefore, it will deal with change.
            (mContext as MainActivity).startActivityForResult(intent, AddDeckActivityStartForResult)
            true
        }

        //send Broadcast to MainActivity for globalViewModel to update the completed parametre for Deck (LocalBroadcastManager for security purposes)
        //isue in non-complete where if ticked to hide it will also automatically check the next box
        holder.checkBox.setOnCheckedChangeListener { view, isChecked ->
            item.completed = isChecked
            Log.i("wow", "is checked: ${isChecked} and the UUID is ${item.id}")
            val intent = Intent().apply {
                setAction(changeCompletedForDeck)
                putExtra(changeCompletedForDeckItemID, item)
            }
            LocalBroadcastManager.getInstance(view.context).sendBroadcast(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeckViewHolder {
        val view = inflater.inflate(R.layout.fragment_deck_item, parent, false)
        return DeckViewHolder(view)
    }

    internal fun setDecks(deck: List<Deck>) {
        this.decks = deck
        notifyDataSetChanged()
    }





    //ViewHolder
    class DeckViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private val title: TextView = v.findViewById(R.id.fragment_deck_item_title)
        private val date: TextView = v.findViewById(R.id.fragment_deck_item_due_date)
        val layout: LinearLayout = v.findViewById(R.id.fragment_deck_item_linearLayout)
        val checkBox: CheckBox = v.findViewById(R.id.fragment_deck_item_selected) //reused so will be buggy for items changed

        fun bind(item: Deck) {
            title.text = item.title
            date.text = Deck.getStringFromCalendar(item.date)
        }

    }
    //constants used for startActivityForResult
    companion object {
        const val AddDeckActivityStartForResult = 1999
        const val DeckPassedItemKey = "deck_passed_from_deck_recycler_view"
        const val changeCompletedForDeck = "deck_passed_changing_boolean_for_completed"
        const val changeCompletedForDeckItemID = "deck_passed_as_item_for_changing_completed_boolean"
    }
}

