package com.piper.swishcards

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var globalViewModel: GlobalViewModel
    private lateinit var contextMenuText: TextView
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

        //Context Menu
        contextMenuText = findViewById(R.id.context_menu_sort_by_text)
        registerForContextMenu(contextMenuText)


        //Recycler view
        recycler = findViewById(R.id.main_activity_reclyer_view)
        adapter = DeckRecyclerAdapter(this)

        //Delete all Decks stored in room ToDo ONLY ACTIVE DURING DEVELOPMENT STAGES
        globalViewModel.deleteAllDecks()


        //Listen to LocalBroadcast from Recycler Item to Update Complete status (from Checkbox)
        /*val myBC: BroadcastReceiver = BroadcastReceiver.PendingResult()*/

        globalViewModel.sortBy(Sort.ALPHA_ASC)

        globalViewModel.allDecks.observeForever { deck ->
            deck?.let { adapter.setDecks(deck) }
        }

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

        //Choose sorting type => opens context menu
        contextMenuText.setOnClickListener {view ->
            openContextMenu(view)
        }

    }


    //sort By Context Menu
    override fun onCreateContextMenu (menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.setHeaderTitle("Choose your option")
        menuInflater.inflate(R.menu.sort_by_deck, menu)
    }

    //What happens when button is clicked. Link to globalViewModel.
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_alpha_asc -> { globalViewModel.sortBy(Sort.ALPHA_ASC) ; contextMenuText.setText(R.string.sort_by_alpha_asc) ; return true; }
            R.id.sort_by_alpha_desc -> { globalViewModel.sortBy(Sort.ALPHA_DES) ; contextMenuText.setText(R.string.sort_by_alpha_des) ; return true; }
            R.id.sort_by_completed_hidden -> { globalViewModel.sortBy(Sort.NON_COM) ; contextMenuText.setText(R.string.sort_by_non_complete) ; return true; }
            R.id.sort_by_due_date -> { globalViewModel.sortBy(Sort.DUE_DATE) ; contextMenuText.setText(R.string.sort_by_due_date) ; return true; }
            else -> return super.onContextItemSelected(item)
        }
    }

    //Dealing /w Adding decks
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

            if (deck?.title == "deleted_object") {
                globalViewModel.delete(deck)
            } else {
                //Update deck
                deck?.let {
                    globalViewModel.update(deck)
                    Log.i("hello", "${deck.date}")
                }
            }
        }
    }
}