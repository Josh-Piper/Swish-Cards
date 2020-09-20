package com.piper.swishcards

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
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
        settingsViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(SettingsViewModel::class.java)

        //Create DialogPrompt to ensure the user wants to delete all Decks
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are You Sure You Want to DELETE All Decks")
            .setPositiveButton("Yes") { x, y ->
                settingsViewModel.deleteAllCompletedDecks(); Toast.makeText(this, "Successfully Delete All Completed Decks", Toast.LENGTH_SHORT).show() } //delete all decks from Repository and Toast to show completition
            .setNegativeButton("No", null)

        Log.i("wow", "global Light mode: ${settingsViewModel.lightMode}")

        //Cache the value from SettingsViewModel according to the current colour sceme
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

            builder.show()
            deleteCompletedDecks.isChecked = false
        }

        lightMode.setOnCheckedChangeListener { compoundButton, isChecked ->
            Log.i("wow", "isChecked: $isChecked")
            settingsViewModel.lightMode = isChecked
            Log.i("wow", "globalView LightMide: ${settingsViewModel.lightMode}")

        }
    }
}