package com.piper.swishcards

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class SettingsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var deleteCompletedDecks: CheckBox
    private lateinit var deleteAllDecks: CheckBox
    private lateinit var lightMode: CheckBox
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

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


        deleteCompletedDecks = findViewById(R.id.delete_complete_decks)
        deleteAllDecks = findViewById(R.id.delete_all_decks)
        lightMode = findViewById(R.id.light_mode_checkbox)
        settingsViewModel = ViewModelProvider(this).get(SettingsViewModel::class.java) //ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(SettingsViewModel::class.java) not needed as there are no additional parametres
        var proceedWithRequest: SETTINGS = SETTINGS.INVALID

        val lightModeKey = getString(R.string.light_mode_pref_key)

        fun updateColourScheme() {
            when (lightMode.isChecked) {
                false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        fun resetAction() { proceedWithRequest = SETTINGS.INVALID; deleteAllDecks.isChecked = false; deleteCompletedDecks.isChecked = false; }

        //Create DialogPrompt to ensure the user wants to delete all Decks
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Please Confirm This Request!").setPositiveButton("Yes") { _, _ ->
            when (proceedWithRequest) {
                SETTINGS.DELETE_COMPLETED -> settingsViewModel.deleteAllCompletedDecks()
                SETTINGS.DELETE_ALL -> settingsViewModel.deleteAllDecks()
                else -> null
            }
            Toast.makeText(this, "Successfully Completed", Toast.LENGTH_SHORT).show()
            resetAction()
            } //delete all decks from Repository and Toast to show completition
            .setNegativeButton("No") { _, _ -> resetAction() }

        //Cache the value from SettingsViewModel according to the current colour sceme
        val sharedPref = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE
        )

        settingsViewModel.darkMode = sharedPref.getBoolean(lightModeKey, false)
        lightMode.isChecked = settingsViewModel.darkMode

        bottomNavigation = findViewById(R.id.bottom_navigation_view)
        bottomNavigation.setOnNavigationItemSelectedListener {
            val intent = when (it.itemId) {
                R.id.nav_settings -> {
                    null;
                    val s = Snackbar.make(
                        bottomNavigation,
                        "Currently on the Settings Screen!",
                        Snackbar.LENGTH_SHORT
                    ); s.setAction("Dismiss") { s.dismiss() }; s.show()
                }
                else -> {
                    onBackPressed(); overridePendingTransition(R.anim.fadein, R.anim.fadeout); }
            }
            try {
                startActivity(intent as Intent?)
            } catch (e: Exception) {
                Log.i("Exception", "Exception: $e occurred")
            }
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            true
        }

        deleteCompletedDecks.setOnClickListener { _ ->
            proceedWithRequest = SETTINGS.DELETE_COMPLETED; builder.show() }

        deleteAllDecks.setOnClickListener { _ ->
            proceedWithRequest = SETTINGS.DELETE_ALL; builder.show() }

        lightMode.setOnCheckedChangeListener { compoundButton, isChecked ->
            sharedPref.edit().putBoolean(lightModeKey, isChecked).apply()
            settingsViewModel.darkMode = sharedPref.getBoolean(lightModeKey, false)
            updateColourScheme()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.drawer_decks -> finish()
            else -> null //do nothing
        }
        return true
    }
}