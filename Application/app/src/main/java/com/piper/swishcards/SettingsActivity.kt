package com.piper.swishcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class SettingsActivity : AppCompatActivity() {
    private lateinit var deleteCompletedDecks: CheckBox
    private lateinit var lightMode: CheckBox
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        deleteCompletedDecks = findViewById(R.id.delete_complete_decks)
        lightMode = findViewById(R.id.light_mode_checkbox)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java)

        Log.i("wow", "global Light mode: ${settingsViewModel.lightMode}")

        lightMode.isChecked = settingsViewModel.lightMode

        bottomNavigation = findViewById(R.id.bottom_navigation_view)
        bottomNavigation.setOnNavigationItemSelectedListener {
            val intent = when (it.itemId) {
                R.id.nav_settings -> { null; val s = Snackbar.make(bottomNavigation, "Currently on the Settings Screen!", Snackbar.LENGTH_SHORT); s.setAction("Dismiss") { s.dismiss() }; s.show()}
                else -> { onBackPressed(); overridePendingTransition(R.anim.fadein, R.anim.fadeout); }
            }
            try { startActivity(intent as Intent?) } catch (e: Exception) { Log.i("Exception", "Exception: $e occurred") }
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            true
        }


        deleteCompletedDecks.setOnCheckedChangeListener { compoundButton, isChecked ->
            //do something
        }

        lightMode.setOnCheckedChangeListener { compoundButton, isChecked ->
            Log.i("wow", "isChecked: $isChecked")
            settingsViewModel.lightMode = isChecked
            Log.i("wow", "globalView LightMide: ${settingsViewModel.lightMode}")

        }
    }
}