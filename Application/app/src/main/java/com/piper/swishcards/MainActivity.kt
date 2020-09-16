package com.piper.swishcards

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var globalViewModel: GlobalViewModel
    private lateinit var fab: FloatingActionButton
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: DeckRecyclerAdapter
    private val AddDeckActivityResultCode = 25


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //basic declarations. UI + ViewModels + RecyclerViews(adapter)
        globalViewModel = ViewModelProvider(this).get(GlobalViewModel::class.java)
        fab = findViewById(R.id.main_activity_fab)
        recycler = findViewById(R.id.main_activity_reclyer_view)
        adapter = DeckRecyclerAdapter(this)

        //Delete all Decks stored in room ToDo ONLY ACTIVE DURING DEVELOPMENT STAGES
        globalViewModel.deleteAllDecks()


        //Listen for livedata changes in ViewModel. if there is, update recycler view
        globalViewModel.allDecks.observe(this, Observer { deck ->
            deck?.let { adapter.setDecks(deck) }
        })

        //Set RecyclerView.
        recycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        fab.setOnClickListener {
            startActivityForResult(
                Intent(this, AddDeckActivity::class.java),
                AddDeckActivityResultCode
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AddDeckActivityResultCode && resultCode == Activity.RESULT_OK) {
            //get Deck from FAB button and insert the new Deck into the ViewModel list
            data?.extras?.getParcelable<Deck>(AddDeckActivity.ADD_DECK_REPLY)?.let { deck ->
                globalViewModel.insert(deck)
            }
        } else if (requestCode == DeckRecyclerAdapter.AddDeckActivityStartForResult && resultCode == Activity.RESULT_OK) {
            //Action sent from longItemClick recycler Item.
            //If position and deck is passed, update the delegated position with modified Deck.
            val deck = data?.extras?.getParcelable<Deck>(AddDeckActivity.ADD_DECK_REPLY)

            //Update deck
            deck?.let {
                globalViewModel.update(deck)
                Log.i("hello", "${deck.date}")
            }
        }




    }

}