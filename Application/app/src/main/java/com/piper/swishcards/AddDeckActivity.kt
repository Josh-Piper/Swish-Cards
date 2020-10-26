package com.piper.swishcards

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.*


class AddDeckActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var doneBtn: Button
    private lateinit var deleteBtn: TextView
    private lateinit var inputTitle: EditText
    private lateinit var inputDate: EditText
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deck)

        //Topbar navigational drawer declarations
        // As stated -> Needs refactoring.
        drawer = findViewById(R.id.drawer)
        topBarNav = findViewById(R.id.topbar_nav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Top Bar / Navigational Drawer logic
        topBarNav.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigational_drawer_open, R.string.navigational_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        topBarNav.setNavigationItemSelectedListener(this)

        //attach all needed widgets to variables
        doneBtn = findViewById(R.id.activity_add_deck_done_button)
        deleteBtn = findViewById(R.id.activity_add_deck_delete_button)
        inputTitle = findViewById(R.id.activity_add_deck_title_input_text)
        inputDate = findViewById(R.id.activity_add_deck_due_input_text)

        //Bottom navigational bar handling
        val firstFragment = BottomBarFragment().apply {
            setScreen(SCREEN.AddDeck)
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_bottom_bar, firstFragment)
            .commit()

        //Hide keyboard from user.
        fun hideKeyboardFromInputText() {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(inputTitle.windowToken, 0)
        }

        //TextWatcher would not work from another class. Current workaround is setting via. the Widgets
        inputTitle.addTextChangedListener(object : TextWatcher {
            val bad_words: MutableList<String> = mutableListOf()

            init { //Resources gathered from www.bannedwordlist.com
                resources.openRawResource(R.raw.swear_words).bufferedReader().forEachLine { line ->
                    bad_words.add(line)
                }
            }

            override fun afterTextChanged(currentText: Editable?) {
                val current: String = currentText.toString()
                val currentBadWords = current.findAnyOf(
                    bad_words,
                    0,
                    ignoreCase = true
                ) //check if current has any bad words in it
                if (current.length > 10) {
                    inputTitle.setText(currentText?.substring(0, 10)) //inputTitle.setSelection(10)
                    hideKeyboardFromInputText()
                    Snackbar.make(
                        findViewById(R.id.content),
                        "Title cannot exceed 10 Characters",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                if (currentBadWords != null) {
                    val newText = current.replace(currentBadWords.second, "", true)
                    inputTitle.setText(newText)
                    hideKeyboardFromInputText()
                    Snackbar.make(
                        findViewById(R.id.content),
                        "Innapropriate Content is Not Allowed!",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        //if activity started from recycler item, then collect the Deck object
        val deck = intent.extras?.getParcelable<Deck>(DeckRecyclerAdapter.DeckPassedItemKey)

        //if item collected is not null, assign the current EditTexts to match it
        if (deck != null) {
            inputTitle.setText(deck.title)
            inputDate.setText(Deck.getStringFromCalendar(deck.date))
        }

        //Set minimum due date to be current date.
        val datePicker = DatePickerDialog(this)
        datePicker.datePicker.apply {
            minDate = Calendar.getInstance().timeInMillis
        }

        //Open DatePickerDialogue instead of inputting text.
        inputDate.setOnClickListener {
            datePicker.show()
            datePicker.setOnDateSetListener { datePicker, year, month, day ->
                //format date to Australian convention and set EditText to it.
                val newDate = Deck.formatDateToAU(day, month, year)
                inputDate.setText(newDate)
            }
        }

        doneBtn.setOnClickListener { view ->
            val replyIntent = Intent()
            val title: String = inputTitle.text.toString()
            val due: String = inputDate.text.toString()

            if  (!(title.isEmpty()) && !(due.isEmpty())) {

                //if Deck was parsed from Recycler Item
                if (deck != null) {
                    deck.title = title
                    deck.date = Deck.getCalendarFromAU(due)
                }

                val addDeck = Deck(
                    title = title,
                    date = Deck.getCalendarFromAU(due),
                    completed = false
                ) //create a new Deck object with the EditText values
                replyIntent.putExtra(ADD_DECK_REPLY, deck ?: addDeck)
                setResult(RESULT_OK, replyIntent)
                finish()
            } else {
                Snackbar.make(
                    view,
                    "Invalid Input! check everything is correct ",
                    Snackbar.LENGTH_LONG
                ).setActionTextColor(
                    Color.RED
                ).show()
            }
        }

        //Delete button changes the title of the current Deck Sends this to MainActivity.
        //Main activity will delete the object if it has that specified name
        //This needs refactoring/a better way for deleting.
        //For instance, a different reply.
        deleteBtn.setOnClickListener {
            if (deck != null) {
                deck.title = "deleted_object"
                val intent = Intent().apply { putExtra(ADD_DECK_REPLY, deck) }
                setResult(RESULT_OK, intent)
                finish()
            }
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

    companion object {
        const val ADD_DECK_REPLY = "com.piper.swishcards.AddDeckActivity.REPLY"
    }
}