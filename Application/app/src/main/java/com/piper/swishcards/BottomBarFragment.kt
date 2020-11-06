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
    private var currentScreen = ArrayDeque<SCREEN>()

    //Set screen manually, used in each activity.
    fun setScreen(screen: SCREEN) {
        currentScreen.push(screen)
    }

    fun closeScreen() {
        currentScreen.pop()
    }

    //Logging purposes
    fun showCurrent() {
        currentScreen.forEach {
            Log.i(MainActivity.GlobalLoggingName, "$it")
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
        bottomBar.setOnNavigationItemSelectedListener { navBtn ->
            val newLoco = when (navBtn.itemId) {
                R.id.nav_settings -> SCREEN.SettingsPage
                R.id.nav_decks -> SCREEN.MainPage
                else -> SCREEN.BACK
            }
            //All changes lead to changeLocation method
            changeLocation(newLoco)
            true
        }
        return view
    }

    //central administration for navigational logic
    private fun changeLocation(newLocation: SCREEN) {
        val compareScreen = currentScreen.peek()
        val intent = when (newLocation) {
            SCREEN.SettingsPage -> { if (compareScreen != SCREEN.SettingsPage) Intent(this.context, SettingsActivity::class.java) else null}
            else -> null
        }
        if (intent == null) {
            //Only close the activity if not on the MainActivity screen
            if (newLocation != SCREEN.MainPage && compareScreen != SCREEN.MainPage) { activity?.onBackPressed() }
            else if (newLocation == SCREEN.BACK) { activity?.onBackPressed() }
            else if (newLocation == SCREEN.MainPage) { activity?.onBackPressed() }

        } else {
            if (compareScreen != SCREEN.MainPage) activity?.onBackPressed()
            startActivity(intent)
        }
    }

    //Used in Activities based on navigation drawer
    fun changeLocationFromDrawer(newLocation: Int) {
        val newLoca: SCREEN = when (newLocation) {
            R.id.drawer_decks -> SCREEN.MainPage
            R.id.drawer_settings -> SCREEN.SettingsPage
            else -> SCREEN.BACK
        }
        changeLocation(newLoca)
    }

    companion object {
        private var bottomBar: BottomBarFragment? = null

        //Singular design pattern. Used as a Stack data type is used. Thus, the same instance should be referenced
        fun get(): BottomBarFragment {
            if (bottomBar == null)
                bottomBar = BottomBarFragment()
            return BottomBarFragment()
        }
    }
}