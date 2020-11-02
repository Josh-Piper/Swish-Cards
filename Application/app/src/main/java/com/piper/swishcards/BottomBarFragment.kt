package com.piper.swishcards

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class BottomBarFragment: Fragment() {

    private lateinit var bottomBar: BottomNavigationView
    //change to stack / pop process Otherwise, will end mainscreen etc.
    private var currentScreen = ArrayDeque<SCREEN>()

    fun setScreen(screen: SCREEN) {
        currentScreen.push(screen)
    }

    fun closeScreen() {
        currentScreen.pop()
    }

    fun showCurrent() {
        currentScreen.forEach {
            Log.i("crazy", "$it")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_bar, container, false).apply {
            bottomBar = findViewById(R.id.bottom_navigation_view)
        }

        //Only have one possible screen set at all times.
        //
        bottomBar.setOnNavigationItemSelectedListener { navBtn ->
            var compareScreen = currentScreen.peek()
            val intent = when (navBtn.itemId) {
                R.id.nav_settings -> { if (compareScreen != SCREEN.SettingsPage) Intent(this.context, SettingsActivity::class.java) else null}
                R.id.nav_decks -> { if (compareScreen != SCREEN.MainPage) Intent(this.context, MainActivity::class.java) else null }
                else -> null
            }
            if (intent == null) {
                //Only close the activity if not on the MainActivity screen
                if (navBtn.itemId != R.id.nav_decks && compareScreen != SCREEN.MainPage) { activity?.onBackPressed() }
                else if (navBtn.itemId == R.id.nav_back) { activity?.onBackPressed() }

            } else {
                if (compareScreen != SCREEN.MainPage) activity?.onBackPressed()
                startActivity(intent)
            }
            true
        }
        return view
    }

    companion object {
        private var bottomBar: BottomBarFragment? = null

        fun get(): BottomBarFragment {
            if (bottomBar == null)
                bottomBar = BottomBarFragment()
            return BottomBarFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}