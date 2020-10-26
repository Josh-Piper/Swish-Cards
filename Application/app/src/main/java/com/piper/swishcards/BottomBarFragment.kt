package com.piper.swishcards

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomBarFragment: Fragment() {

    private lateinit var bottomBar: BottomNavigationView
    private var currentScreen: SCREEN? = null

    fun setScreen(screen: SCREEN) {
        currentScreen = screen
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_bar, container, false).apply {
            bottomBar = findViewById(R.id.bottom_navigation_view)
        }

        bottomBar.setOnNavigationItemSelectedListener { navBtn ->
            val intent = when (navBtn.itemId) {
                R.id.nav_settings -> { if (currentScreen != SCREEN.SettingsPage) Intent(this.context, SettingsActivity::class.java) else null}
                R.id.nav_decks -> null
                else -> null
            }
            if (currentScreen != SCREEN.MainPage) activity?.onBackPressed()
            if (intent != null) startActivity(intent)
            true
        }
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}