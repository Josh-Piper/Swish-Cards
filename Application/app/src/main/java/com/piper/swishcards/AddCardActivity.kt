package com.piper.swishcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class AddCardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var deleteBtn: TextView
    private lateinit var doneButton: Button
    private lateinit var cardQuestion: EditText
    private lateinit var cardAnswer: EditText
    private lateinit var cardType: Spinner
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        val deck = intent.extras?.getParcelable<Deck>(FlashCardsOverview.passDeckToCreateNewCard)

        if (deck == null) { finish(); Log.i("wow", "error occurred getting Deck at AddCardActivity")} //if no Deck passed through, prompt error.

        deleteBtn = findViewById(R.id.activity_add_card_delete_button)
        doneButton = findViewById(R.id.activity_add_card_done_button)
        cardQuestion = findViewById(R.id.activity_add_card_question)
        cardAnswer = findViewById(R.id.activity_add_card_answer)
        cardType = findViewById(R.id.activity_add_card_type_of_question)

        //Topbar navigational drawer
        drawer = findViewById(R.id.nav_drawer)
        topBarNav = findViewById(R.id.topbar_nav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Logic
        topBarNav.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigational_drawer_open, R.string.navigational_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        topBarNav.setNavigationItemSelectedListener(this)


        //cardType to spinner options
        val adapter = ArrayAdapter(applicationContext, R.layout.spinner_layout, CardType.values())
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item)
        cardType.adapter = adapter

        //Bottom navbar implementation
        bottomNavigation = findViewById(R.id.bottom_navigation_view)
        bottomNavigation.setOnNavigationItemSelectedListener { navButton ->
            val intent = when (navButton.itemId) {
                R.id.nav_settings -> Intent(this, SettingsActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
                R.id.nav_decks -> Intent(this, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) }
                R.id.nav_back -> null
                else -> null //do nothing as current setting is MainActivity
            }
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            true
        }

        doneButton.setOnClickListener { view ->

            //check the spinner option. Only Short_Answer currently supported. Snackbar... (an action to automatically change it to make it easier for a user.)
            if (!cardType.equals(CardType.SHORT_ANSWER)) {
                val snack = Snackbar.make(view, "Short Answer is currently ONLY Supported", Snackbar.LENGTH_SHORT)
                snack.setAction("SET") { view ->
                    cardType.setSelection(0)
                }
                snack.show()
            } else {
                deck?.apply {
                    val parentID = deck.id
                    val card = FlashCard(pid = parentID, question = cardQuestion.text.toString(), answer = cardAnswer.text.toString(), type = CardType.SHORT_ANSWER, completed = false)
                    val intent = Intent().putExtra(addCardReply, card)
                    setResult(RESULT_OK, intent)
                    //TO DO -> Validation
                }
            }
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

    companion object {
        const val addCardReply = "com.piper.swishcards.AddCardActivity.REPLY"
    }
}