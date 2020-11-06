package com.piper.swishcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.*
import kotlin.collections.ArrayList

class FlashCardsOverview : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AddCardCallback {

    //Declarations
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recycler: RecyclerView
    private lateinit var cardsViewModel: CardViewModel
    private lateinit var fab: FloatingActionButton
    private lateinit var topBarTitle: TextView
    private lateinit var adapters: FlashCardRecyclerView
    private lateinit var firstFragment: BottomBarFragment
    private lateinit var startBtn: Button
    private lateinit var toastContext: LinearLayout
    private lateinit var startCards: List<FlashCard>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_cards_overview)

        topBarTitle = findViewById(R.id.topbar_title)
        startBtn = findViewById(R.id.flash_cards_overview_start_btn)
        toastContext = findViewById(R.id.content)

        //Topbar navigational drawer declarations
        drawer = findViewById(R.id.drawer)
        topBarNav = findViewById(R.id.topbar_nav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //declare ViewModel
        cardsViewModel = ViewModelProvider(this).get(CardViewModel::class.java)


        //Set RecyclerView.
        adapters = FlashCardRecyclerView(this, this)
        recycler = findViewById(R.id.recycler)
        recycler.apply {
            layoutManager = LinearLayoutManager(this@FlashCardsOverview)
            adapter = adapters
        }

        //dynamic observation
        cardsViewModel.deck = intent.extras?.getParcelable(DeckRecyclerAdapter.passDeckToFlashCardOverview)

        //Link to ViewModel for orientation changes
        if (cardsViewModel.deck != null) {
            //set the top bar message
            val message = String.format(getString(R.string.top_bar_title_message), cardsViewModel.deck?.title)
            topBarTitle.setText(message)

            //Sorting Cards only by parent ID
            cardsViewModel.sortCards(SortCard.PARENT_ID, cardsViewModel.deck?.id ?: UUID.randomUUID())

        } else {
            finish()
            Log.i(MainActivity.GlobalLoggingName, "Error occurred")
        }

        //Update the UO when changes are made to the LiveData, startCards are assigned as passing issue current persists
        cardsViewModel.allCards.observeForever { cards ->
            cardsViewModel.deck?.let { adapters.setCards(cards) }
            startCards = cards
        }

        //FAB
        fab = findViewById(R.id.flash_cards_overview_fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddCardActivity::class.java).apply {
                putExtra(passDeckToCreateNewCard, cardsViewModel.deck)
            }

            startActivityForResult(intent, createNewCardFromAddCardRequestCode)
        }


        registerForContextMenu(startBtn)

        startBtn.setOnClickListener {
            openContextMenu(startBtn)

        }

        //Top Bar / Navigational Drawer logic
        topBarNav.bringToFront()
        val toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar,
            R.string.navigational_drawer_open,
            R.string.navigational_drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        topBarNav.setNavigationItemSelectedListener(this)

        //Inflate bottom navigational view
        firstFragment = BottomBarFragment.get().apply {
            setScreen(SCREEN.CardPage)
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_bottom_bar, firstFragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        firstFragment.changeLocationFromDrawer(item.itemId)
        return true
    }

    //Updates the FlashCard via. the ViewModel.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == createNewCardFromAddCardRequestCode && resultCode == RESULT_OK) {
            data?.extras?.getParcelable<FlashCard>(AddCardActivity.ADD_CARD_REPLY)
                ?.let { card ->
                    cardsViewModel.insertCard(card)
                    Log.i(MainActivity.GlobalLoggingName, "Card Owner: ${card.pid}")
                }
        } else if (requestCode == FlashCardRecyclerView.AddCardkActivityStartForResult && resultCode == RESULT_OK) {
            //update or delete card depending on action
            data?.extras?.getParcelable<FlashCard>(AddCardActivity.ADD_CARD_REPLY)
                ?.let { card ->
                    if (card.question == "deleted_object") cardsViewModel.deleteCard(card) else cardsViewModel.updateCard(
                        card
                    )
                }
        }
    }

    override fun onUpdateCard(card: FlashCard) {
        //only used to update card boolean value of completed.
        cardsViewModel.updateCard(card)
    }

    override fun onBackPressed() {
        firstFragment.closeScreen()
        super.onBackPressed()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {

        menuInflater.inflate(R.menu.review, menu)
    }

    //Only start a reviewal if review_all is clicked. As no other method is implemented..
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.review_all -> {
                cardsViewModel.deck?.apply {
                    val intent = Intent(this@FlashCardsOverview, FlashCardReview::class.java)
                    if (startCards?.size > 0) {
                        intent.putParcelableArrayListExtra(
                            reviewCardsKey,
                            startCards as ArrayList<FlashCard>
                        )
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            this@FlashCardsOverview,
                            "Error Occurred, Please Add Cards.",
                            Toast.LENGTH_SHORT
                        ).show()
                         }
                    }
                return super.onContextItemSelected(item)}
            else -> {
                Toast.makeText(toastContext.context, "No Implemented Yet", Toast.LENGTH_SHORT)
                    .show(); return super.onContextItemSelected(item); }
        }
    }

    companion object {
        const val createNewCardFromAddCardRequestCode = 1765
        const val reviewCardsKey = "com.piper.josh.swish.cards.pass_deck_of_cards_for_review"
        const val passDeckToCreateNewCard = "com.piper.josh.swish.cards.pass_deck_to_add_card"
    }
}