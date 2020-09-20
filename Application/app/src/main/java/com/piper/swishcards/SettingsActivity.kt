package com.piper.swishcards

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox

class SettingsActivity : AppCompatActivity() {
    private lateinit var deleteCompletedDecks: CheckBox
    private lateinit var lightMode: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        deleteCompletedDecks = findViewById(R.id.delete_complete_decks)
        lightMode = findViewById(R.id.light_mode_checkbox)

        deleteCompletedDecks.setOnCheckedChangeListener { compoundButton, isChecked ->
            //do something
        }

        lightMode.setOnCheckedChangeListener { compoundButton, isChecked ->
            //do something
        }
    }
}