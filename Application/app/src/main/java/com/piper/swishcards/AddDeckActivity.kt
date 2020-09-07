package com.piper.swishcards

import android.app.Activity
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

class AddDeckActivity : AppCompatActivity() {
    private lateinit var doneBtn: Button
    private lateinit var inputTitle: EditText
    private lateinit var inputDate: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deck)

        doneBtn = findViewById(R.id.activity_add_deck_done_button)
        inputTitle = findViewById(R.id.activity_add_deck_title_input_text)
        inputDate = findViewById(R.id.activity_add_deck_due_input_text)


        inputDate.setOnClickListener {
            //DatePickerDialogue. Convert to Text for Flash Card DECK class
        }

        doneBtn.setOnClickListener {view ->
            val replyIntent = Intent()
            var title: String = inputTitle.text.toString()
            var due: String = inputDate.text.toString()



            Log.i("TAG", "")

            if (!title.isNullOrEmpty()) {
                val addDeck =
                    Deck(title = inputTitle.text.toString()) //issues with replicacing the title
                replyIntent.putExtra(ADD_DECK_REPLY, addDeck)
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
    }
}