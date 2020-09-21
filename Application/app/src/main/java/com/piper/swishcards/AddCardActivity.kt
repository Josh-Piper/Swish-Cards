package com.piper.swishcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class AddCardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var deleteBtn: Button
    private lateinit var addCard: Button
    private lateinit var cardQuestion: EditText
    private lateinit var cardAnswer: EditText
    private lateinit var cardType: Spinner
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_card)

        deleteBtn = findViewById(R.id.activity_add_card_delete_button)
        addCard = findViewById(R.id.activity_add_card_done_button)
        cardQuestion = findViewById(R.id.activity_add_card_question)
        cardAnswer = findViewById(R.id.activity_add_card_answer)
        cardType = findViewById(R.id.activity_add_card_type_of_question)

        //Topbar navigational drawer
        drawer = findViewById(R.id.drawer)
        topBarNav = findViewById(R.id.topbar_nav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Logic
        topBarNav.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigational_drawer_open, R.string.navigational_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        topBarNav.setNavigationItemSelectedListener(this)

        //Bottom navbar implementation
        bottomNavigation = findViewById(R.id.bottom_navigation_view)
        bottomNavigation.setOnNavigationItemSelectedListener { navButton ->
            val intent = when (navButton.itemId) {
                R.id.nav_settings -> Intent(this.baseContext, SettingsActivity::class.java)
                else -> null //do nothing as current setting is MainActivity
            }
            try { startActivity(intent as Intent?) } catch (e: Exception) { Log.i("Exception", "Exception: $e occurred") }
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            finish()
            true
        }


    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.drawer_settings -> { finish(); startActivity(Intent(this, SettingsActivity::class.java))}
            R.id.drawer_decks -> { finish() }
            else -> null //do nothing
        }
        return true
    }
}