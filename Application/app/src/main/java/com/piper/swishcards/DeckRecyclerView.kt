package com.piper.swishcards

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
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
        holder.bind(item)



        //manages individual (set here to allow prioritising different decks, i.e. dates)
        holder.layout.setOnLongClickListener {view ->
            val intent = Intent(view.context, AddDeckActivity::class.java).apply {
                putExtra(DeckPassedItemKey, item)
            }
            //cast to MainActivity since it is observing any changes to Decks. Therefore, it will deal with change.
            (mContext as MainActivity).startActivityForResult(intent, AddDeckActivityStartForResult)
            true
        }


/*        holder.layout.setOnClickListener {view ->
            //can do this as not expecting anything new. Not looking for a result etc. so start from the onClick event
            view.context.startActivity(intent)
        }*/
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

        fun bind(item: Deck) {
            title.text = item.title
            date.text = item.date
        }

    }
    //constants used for startActivityForResult
    companion object {
        const val AddDeckActivityStartForResult = 1999
        const val DeckPassedItemKey = "deck_passed_from_deck_recycler_view"
    }
}

