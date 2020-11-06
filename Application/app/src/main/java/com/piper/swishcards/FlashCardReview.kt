package com.piper.swishcards

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import org.w3c.dom.Text

class FlashCardReview : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CardReviewalCallbacks {

    private lateinit var questionText: TextView
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var topBarTitle: TextView
    private lateinit var nextBtn: Button
    private lateinit var firstFragment: BottomBarFragment
    private lateinit var flashCardViewModel: FlashCardViewModel
    private lateinit var answerInput: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var snackContext: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_card_review)

        topBarTitle = findViewById(R.id.topbar_title)
        topBarTitle.setText("Reviewing Cards")

        nextBtn = findViewById(R.id.activity_flash_card_review_next_button)
        answerInput = findViewById(R.id.activity_flash_card_review_edit_text)
        progressBar = findViewById(R.id.activity_flash_card_review_progress_bar)
        questionText = findViewById(R.id.activity_flash_card_review_question)
        snackContext = findViewById(R.id.activity_flash_card_review_context)

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

        //Information needed to iterate through the different cards
        val cards = intent.extras?.getParcelableArrayList<Parcelable>(FlashCardsOverview.reviewCardsKey)

        //declare ViewModel
        flashCardViewModel = ViewModelProvider(this).get(FlashCardViewModel::class.java)
        flashCardViewModel.setDependencies((cards?.toList() as List<FlashCard>), this)

    //Next button logic.
        nextBtn.setOnClickListener { _ ->
            flashCardViewModel.incrementCard(answerInput.text.toString())
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

    override fun increaseProgressBar() {
        progressBar.progress++
    }

    override fun setQuestionText(question: String) {
        questionText.setText(question)
    }

    override fun setNextButtonText(text: String) {
        nextBtn.setText(text)
    }

    override fun finishedReviewal(message: String) {
        val snack = Snackbar.make (snackContext, message, Snackbar.LENGTH_INDEFINITE)
        snack.setAction("FINISHED") { _ ->
            finish()
        }
        //changes snackBar's max lines
        val v = snack.view
        val tv = v.findViewById<View>(com.google.android.material.R.id.snackbar_text) as TextView
        tv.maxLines = 5
        snack.show()
    }

    override fun setProgressMax(max: Int) {
        progressBar.max = max
    }

    override fun onBackPressed() {
        firstFragment.closeScreen()
        super.onBackPressed()
    }
}