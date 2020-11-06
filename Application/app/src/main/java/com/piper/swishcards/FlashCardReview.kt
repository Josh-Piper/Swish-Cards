package com.piper.swishcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import org.w3c.dom.Text

class FlashCardReview : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var questionText: TextView
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var topBarTitle: TextView
    private lateinit var nextBtn: Button
    private lateinit var firstFragment: BottomBarFragment
    private lateinit var answerInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card_review)

        topBarTitle = findViewById(R.id.topbar_title)
        topBarTitle.setText("Reviewing Cards")

        nextBtn = findViewById(R.id.activity_flash_card_review_next_button)
        answerInput = findViewById(R.id.activity_flash_card_review_edit_text)

        //Topbar navigational drawer declarations
        drawer = findViewById(R.id.drawer)
        topBarNav = findViewById(R.id.topbar_nav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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
            setScreen(SCREEN.REVIEWAL)
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_bottom_bar, firstFragment)
            .commit()

        questionText = findViewById(R.id.activity_flash_card_review_question)


        //dynamic observation
        //Information needed to iterate through the different cards
        val cards = intent.extras?.getParcelableArrayList<Parcelable>(FlashCardsOverview.reviewCardsKey)
        val card = (cards?.toList() as List<FlashCard>)
        var currentCard = 0
        val maxCards = card.size - 1

        questionText.setText(card[currentCard].question)

    //Next button logic.
        nextBtn.setOnClickListener {view ->
            if (!(currentCard + 1 > maxCards)) {
                currentCard++
                questionText.setText(card[currentCard].question)
            }
            if (currentCard == maxCards) nextBtn.setText("Finish")
            if (nextBtn.text == "Finish" && currentCard == maxCards) finish()

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.drawer_decks -> finish()
            R.id.drawer_settings -> {
                finish(); startActivity(Intent(this, SettingsActivity::class.java))
            }
            else -> finish() 
        }
        return true
    }


    override fun onBackPressed() {
        firstFragment.closeScreen()
        super.onBackPressed()
    }
}