package com.piper.swishcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class AddCardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //Declare UI aspects of the application
    private lateinit var deleteBtn: TextView
    private lateinit var doneButton: Button
    private lateinit var cardQuestion: EditText
    private lateinit var cardAnswer: EditText
    private lateinit var cardType: Spinner
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var firstFragment: BottomBarFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        //Attaching the UI aspects to their findViewById.
        deleteBtn = findViewById(R.id.activity_add_card_delete_button)
        doneButton = findViewById(R.id.activity_add_card_done_button)
        cardQuestion = findViewById(R.id.activity_add_card_question)
        cardAnswer = findViewById(R.id.activity_add_card_answer)
        cardType = findViewById(R.id.activity_add_card_type_of_question)

        //Topbar navigational drawer. Associating their widgets
        drawer = findViewById(R.id.nav_drawer)
        topBarNav = findViewById(R.id.topbar_nav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Logic for the navigational drawer. This is currently copied and pasted code. Should be moved out into a possible? fragment?
        //possible issues as a Fragment is contained to a certain space. Thus, the drawer won't be able to fit the screen in height.
        //Needs to be refactored
        topBarNav.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigational_drawer_open, R.string.navigational_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        topBarNav.setNavigationItemSelectedListener(this)


        //Assign a Spinner for the CardType. The adapter relies on the CardType values. Acts as validation
        val adapter = ArrayAdapter(applicationContext, R.layout.spinner_layout, CardType.values())
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        cardType.adapter = adapter

        //Bottom navbar implementation
        //Inflate bottom navigational view
        firstFragment = BottomBarFragment.get().apply {
            setScreen(SCREEN.CardPage)
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_bottom_bar, firstFragment)
            .commit()


        //Get Deck from FlashCardsOverview. If no Deck was passed through. Then finish().
        //This AddCardActivity requires the parentID. Thus, it should not operate unless the parent Deck is passed.
        val deck = intent.extras?.getParcelable<Deck>(FlashCardsOverview.passDeckToCreateNewCard)

        //Get FlashCard from recycler view from a long on Click
        val card = intent.extras?.getParcelable<FlashCard>(FlashCardRecyclerView.CardPassedItemKey)

        if (card != null) {
            cardType.setSelection(adapter.getPosition(card.type))
            cardQuestion.setText(card.question)
            cardAnswer.setText(card.answer)
        } else if (deck == null) {
            finish() //An error occurred. (Log, if need be)
        }



        //Delete if a pre-existing card was selected.
        deleteBtn.setOnClickListener { _ ->
            if (card != null) {
                card.question = "deleted_object"
                val intent = Intent().apply { putExtra(ADD_CARD_REPLY, card) }
                setResult(RESULT_OK, intent)
                finish()
            }
        }


        doneButton.setOnClickListener { view ->

          /*  //validation
            //check the spinner option. Only Short_Answer currently supported. Snackbar... (an action to automatically change it to make it easier for a user.)
            if (!cardType.selectedItem.equals(CardType.SHORT_ANSWER)) {
                val snack = Snackbar.make (view, "Short Answer is currently ONLY Supported", Snackbar.LENGTH_SHORT)
                snack.setAction("SET") { view ->
                    cardType.setSelection(0)
                }
                snack.show()
                return@setOnClickListener
            }


            //validation
            //validation should be moved to a text watcher
            //or external class
            if (cardQuestion.text.length > 15 || cardAnswer.text.length > 15) {
                val snack = Snackbar.make (view, "Short Answer is currently ONLY Supported", Snackbar.LENGTH_SHORT)
                snack.setAction("SET") { view ->
                    cardQuestion.text.substring(0, 15)
                    cardAnswer.text.substring(0, 15)
                }
                snack.show()
                return@setOnClickListener
            }

            if (cardQuestion.text.isNullOrEmpty() || cardAnswer.text.isNullOrEmpty()) {
                Snackbar.make (view, "Question and Answer must NOT be empty!", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }*/
                card?.apply {
                    question = cardQuestion.text.toString()
                    answer = cardAnswer.text.toString()
                }
           //if no pre-existing card passed through. Assign newCard to a FlashCard()
                val newCard = if (deck != null) {
                    FlashCard(
                    pid = deck.id,
                    question = cardQuestion.text.toString(),
                    answer = cardAnswer.text.toString(),
                    type = CardType.SHORT_ANSWER,
                    completed = false
                )} else { null }

                val intent = Intent().putExtra(ADD_CARD_REPLY, card ?: newCard)
                setResult(RESULT_OK, intent)
                finish()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent = when (item.itemId) {
            R.id.drawer_settings -> Intent(this, SettingsActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
            R.id.drawer_decks -> Intent(this, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
            else -> null //do nothing
        }
        startActivity(intent)
        return true
    }

    override fun onBackPressed() {
        firstFragment.closeScreen()
        super.onBackPressed()
    }

    companion object {
        const val ADD_CARD_REPLY = "com.piper.swishcards.AddCardActivity.REPLY"
    }
}