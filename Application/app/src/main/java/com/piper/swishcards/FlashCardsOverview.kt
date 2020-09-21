package com.piper.swishcards

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class FlashCardsOverview : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var recycler: FlashCardRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash_cards_overview)

        //Topbar navigational drawer declarations
        drawer = findViewById(R.id.drawer)
        topBarNav = findViewById(R.id.topbar_nav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //get passed value
        val deck = intent.extras?.getParcelable<Deck>(DeckRecyclerAdapter.passDeckToFlashCardOverview)

        if (deck != null) {
            //print all FlashCards existing to the recycler view
        }

        //Set RecyclerView.


        //FAB
        //ADD NEW FLASH CARD BUTTON!


        

        //Top Bar / Navigational Drawer logic
        topBarNav.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigational_drawer_open, R.string.navigational_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        topBarNav.setNavigationItemSelectedListener(this)

        //Bottom navigational bar handling
        bottomNavigation = findViewById(R.id.bottom_navigation_view)
        bottomNavigation.setOnNavigationItemReselectedListener {
            val intent = when (it.itemId) {
                R.id.nav_decks -> { finish() } //do nothing as current setting is MainActivity
                R.id.nav_settings -> { finish(); Intent(this.baseContext, SettingsActivity::class.java) }
                R.id.nav_back -> { finish() }
                else -> null
            }
            startActivity(intent as Intent?)
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
}