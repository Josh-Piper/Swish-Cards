package com.piper.swishcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class FlashCardsOverview : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var recycler: RecyclerView
    private lateinit var cardsViewModel: CardViewModel
    private lateinit var fab: FloatingActionButton
    private lateinit var topBarTitle: TextView
    private lateinit var adapters: FlashCardRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_cards_overview)

        topBarTitle = findViewById(R.id.topbar_title)

        //Topbar navigational drawer declarations
        drawer = findViewById(R.id.drawer)
        topBarNav = findViewById(R.id.topbar_nav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //declare ViewModel
        cardsViewModel = ViewModelProvider(this).get(CardViewModel::class.java)

        //get passed value
        val deck = intent.extras?.getParcelable<Deck>(DeckRecyclerAdapter.passDeckToFlashCardOverview)

        if (deck != null) {
            //set the top bar message
            val message = String.format(getString(R.string.top_bar_title_message), deck.title)
            topBarTitle.setText(message)

            //print all FlashCards existing to the recycler view
            //if nothing passed then do nothing. This shouldnt work otherwies. Prompt error
        } else {
            finish()
            Log.i("wow", "Error occurred")
        }

        //Set RecyclerView.
        adapters = FlashCardRecyclerView(this)
        recycler = findViewById(R.id.recycler)
        recycler.apply {
            layoutManager = LinearLayoutManager(this@FlashCardsOverview)
            adapter = adapters
        }

        //dynamic observation
        cardsViewModel.allCards.observeForever { cards ->
            deck?.let { adapters.setCards(cards) }
        }

        //FAB
        fab = findViewById(R.id.flash_cards_overview_fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java).apply {
                putExtra(passDeckToCreateNewCard, deck) }

            startActivityForResult(intent, createNewCardFromAddCardRequestCode)
        }

        //use call back function

        

        //Top Bar / Navigational Drawer logic
        topBarNav.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigational_drawer_open, R.string.navigational_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        topBarNav.setNavigationItemSelectedListener(this)

        //Bottom navigational bar handling
        bottomNavigation = findViewById(R.id.bottom_navigation_view)
        bottomNavigation.setOnNavigationItemSelectedListener {navBtn ->
            val intent = when (navBtn.itemId) {
                R.id.nav_settings -> Intent(this.baseContext, SettingsActivity::class.java)
                else -> null
            }
            if (intent != null) startActivity(intent)
            finish()
            true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.drawer_decks -> finish()
            R.id.drawer_settings -> { finish(); startActivity(Intent(this, SettingsActivity::class.java)) }
            else -> null //do nothing
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == createNewCardFromAddCardRequestCode && resultCode == RESULT_OK) {
            data?.extras?.getParcelable<FlashCard>(AddCardActivity.addCardReply)?.let { card ->
                cardsViewModel.insertCard(card)
                //Add card to the deck
            }
        }
    }

    companion object {
        const val createNewCardFromAddCardRequestCode = 1765
        const val passDeckToCreateNewCard = "com.piper.josh.swish.cards.pass_deck_to_add_card"
    }
}