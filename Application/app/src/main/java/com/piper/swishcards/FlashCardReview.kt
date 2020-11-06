package com.piper.swishcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class FlashCardReview : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, CardReviewalCallbacks {

    //Declarations
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

        //Setup the top bar title
        topBarTitle = findViewById(R.id.topbar_title)
        topBarTitle.setText("Reviewing Cards")

        //Assign variables to widgets
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

        //declare ViewModel and sync all data required to handle orientation changes
        flashCardViewModel = ViewModelProvider(this).get(FlashCardViewModel::class.java)
        flashCardViewModel.setDependencies((cards?.toList() as List<FlashCard>), this)

    //Next button logic.
        nextBtn.setOnClickListener { _ ->
            //Check answer and increase/move to next card.
            flashCardViewModel.incrementCard(answerInput.text.toString())
        }
    }

    //Sync the TopNaviBar with the BottomNaviBar logic
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        firstFragment.changeLocationFromDrawer(item.itemId)
        return true
    }

    //The following are callbacks activated via. the ViewModel.
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
        //changes snackBar's max lines (otherwise it will show ellipses)
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