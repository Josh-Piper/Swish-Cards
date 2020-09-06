package com.piper.swishcards

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        fab = findViewById(R.id.main_activity_fab)
        recycler = findViewById(R.id.main_activity_reclyer_view)
        adapter = DeckRecyclerAdapter(this)

        globalViewModel = ViewModelProvider(this).get(GlobalViewModel::class.java)
        globalViewModel.deleteAllDecks()
        globalViewModel.allDecks.observe(this, Observer { deck ->
            deck?.let { adapter.setDecks(deck) }
        })

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
            data?.extras?.getParcelable<Deck>(AddDeckActivity.ADD_DECK_REPLY)?.let { deck ->
                globalViewModel.insert(deck)
            }
        }
    }
}