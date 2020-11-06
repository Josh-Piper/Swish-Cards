package com.piper.swishcards

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.util.*


class AddDeckActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, ValidateCallback, AddDeckCallBack {

    //Declarations
    private lateinit var doneBtn: Button
    private lateinit var deleteBtn: TextView
    private lateinit var inputTitle: EditText
    private lateinit var inputDate: EditText
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var firstFragment: BottomBarFragment
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var toastContext: LinearLayout
    private lateinit var addDeckViewModel: AddDeckViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deck)

        //Topbar navigational drawer declarations
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
        toastContext = findViewById(R.id.activty_add_deck_content)

        //Assign ViewModel to cater for a stage change, necessary to remember the Deck passed in.
        addDeckViewModel = ViewModelProvider(this).get(AddDeckViewModel::class.java)

        //Bottom navigational bar handling
        firstFragment = BottomBarFragment.get().apply {
            setScreen(SCREEN.AddDeck)
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container_bottom_bar, firstFragment)
            .commit()


        //TextWatcher declaration. Don't allow swear words or length of words > 10
        resources.openRawResource(R.raw.swear_words).bufferedReader().forEachLine { line ->
            addDeckViewModel.bad_words.add(line)
        }

        val watcher = ValidatingWatcher(this).apply {
            setWords(addDeckViewModel.bad_words)
        }

        inputTitle.addTextChangedListener(watcher)

        //if activity started from recycler item, then collect the Deck object and sync to ViewModel
        val d = intent.extras?.getParcelable<Deck>(DeckRecyclerAdapter.DeckPassedItemKey)
        addDeckViewModel.init(d, this)

        //Set minimum due date to be current date (to the millisecond).
        val datePicker = DatePickerDialog(this)
        datePicker.datePicker.apply {
            minDate = Calendar.getInstance().timeInMillis
        }

        //Open DatePickerDialogue instead of inputting text. Used for form validation.
        inputDate.setOnClickListener {
            datePicker.show()
            datePicker.setOnDateSetListener { datePicker, year, month, day ->
                //format date to Australian convention and set EditText to it.
                val newDate = Deck.formatDateToAU(day, month, year)
                inputDate.setText(newDate)
            }
        }

        //Logic occurs in ViewModel. Used for creating a new deck or updating pre-existing deck
        doneBtn.setOnClickListener { view ->
            val replyIntent = Intent()
            val title: String = inputTitle.text.toString()
            val due: String = inputDate.text.toString()

                //if Deck was parsed from Recycler Item
                if (d != null) {
                    addDeckViewModel.setDeck(title, due)
                }

                if (addDeckViewModel.isValidationCorrect(title, due)) {
                //create a new Deck object with the EditText values if d does not exist.
                replyIntent.putExtra(ADD_DECK_REPLY, addDeckViewModel.getDeck(title, due))
                setResult(RESULT_OK, replyIntent)
                finish()
                }
            }

        //Delete button changes the title of the current Deck. Send this to MainActivity
        //Main activity will delete the object if it has that specified name
        //This needs refactoring/a better way for deleting.
        //For instance, setting the UUID to a specific UUID (hard to spoof)
        deleteBtn.setOnClickListener {
            if (addDeckViewModel.deck != null) {
                addDeckViewModel.deck?.title = "deleted_object"
                val intent = Intent().apply { putExtra(ADD_DECK_REPLY, addDeckViewModel.deck) }
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    //Synchronise the TopNaviBar with BottomNavi logic
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        firstFragment.changeLocationFromDrawer(item.itemId)
        return true
    }

    //Hide keyboard from user.
    fun hideKeyboardFromInputText() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(inputTitle.windowToken, 0)
    }

    //Set text sent from TextWatcherCallback
    override fun setText(message: String) {
        inputTitle.setText(message)
    }

    //TextWatcher callback
    override fun showToast(message: String) {
        Toast.makeText(toastContext.context, message, Toast.LENGTH_SHORT).show()
        hideKeyboardFromInputText()
    }

    //Below is the callbacks used for the ViewModel
    override fun setSnackBar(message: String) {
        Snackbar.make(toastContext, message, Snackbar.LENGTH_LONG).setActionTextColor(Color.RED).show()
    }
    override fun setDateFromModel(message: String) {
        inputDate.setText(message)
    }

    override fun setTitleFromModel(message: String) {
        inputTitle.setText(message)
    }

    override fun onBackPressed() {
        firstFragment.closeScreen()
        super.onBackPressed()
    }

    companion object {
        const val ADD_DECK_REPLY = "com.piper.swishcards.AddDeckActivity.REPLY"
    }
}