package com.piper.swishcards

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import java.util.*

class AddDeckActivity : AppCompatActivity() {
    private lateinit var doneBtn: Button
    private lateinit var inputTitle: EditText
    private lateinit var inputDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deck)

        //attach all needed widgets to variables
        doneBtn = findViewById(R.id.activity_add_deck_done_button)
        inputTitle = findViewById(R.id.activity_add_deck_title_input_text)
        inputDate = findViewById(R.id.activity_add_deck_due_input_text)

        //if activity started from recycler item, then collect the Deck object
        val deck = intent.extras?.getParcelable<Deck>(DeckRecyclerAdapter.DeckPassedItemKey)

        //if item collected is not null, assign the current EditTexts to match it
        if (deck != null) {
            inputTitle.setText(deck.title)
            inputDate.setText(deck.date)
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

        doneBtn.setOnClickListener {view ->
            val replyIntent = Intent()
            var title: String = inputTitle.text.toString()
            var due: String = inputDate.text.toString()

            if  (!(title.isNullOrEmpty()) && !(due.isNullOrEmpty())) {

                //if Deck was parsed from Recycler Item
                if (deck != null) {
                    deck.title = title
                    deck.date = due
                    Log.i("hello", "${deck.date}")
                }

                val addDeck = Deck(title = title, date = due) //create a new Deck object with the EditText values
                replyIntent.putExtra(ADD_DECK_REPLY, deck ?: addDeck)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            } else {
                Snackbar.make(view, "Invalid Input! check everything is correct ", Snackbar.LENGTH_LONG).setActionTextColor(
                    Color.RED).show()
            }


        }
    }

    companion object {
        const val ADD_DECK_REPLY = "com.piper.swishcards.AddDeckActivity.REPLY"
        const val MODIFY_DECK_REPLY = "com.piper.swishcards.AddDeckActivity.MODIFY.REPLY"
    }
}