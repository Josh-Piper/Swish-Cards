package com.piper.swishcards

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider

class AddDeckActivity : AppCompatActivity() {
    private lateinit var doneBtn: Button
    private lateinit var inputTitle: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_deck)

        doneBtn = findViewById(R.id.activity_add_deck_done_button)
        inputTitle = findViewById(R.id.activity_add_deck_title_input_text)

        doneBtn.setOnClickListener {
            val replyIntent = Intent()

            if (!doneBtn.text.isNullOrEmpty()) {
                val addDeck =
                    Deck(title = inputTitle.text.toString()) //issues with replicacing the title
                replyIntent.putExtra(ADD_DECK_REPLY, addDeck)
                setResult(Activity.RESULT_OK, replyIntent)
                finish()
            } else {
                Toast.makeText(this, "Invalid Input", Toast.LENGTH_SHORT)
                    .show() //replace with snack bar
            }
        }
    }

    companion object {
        const val ADD_DECK_REPLY = "com.piper.swishcards.AddDeckActivity.REPLY"
    }
}