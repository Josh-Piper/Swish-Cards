package com.piper.swishcards

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.PrecomputedText
import android.util.Log
import android.view.ContextMenu
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import java.security.Policy

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var globalViewModel: GlobalViewModel
    private lateinit var contextMenuText: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: DeckRecyclerAdapter
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val AddDeckActivityResultCode = 25
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        topBarNav.setCheckedItem(R.id.drawer_decks)


        Log.i("wow", "I am the creator of all things and AM CALLED!")

        //Get current colour scenario
        val lightModeKey = getString(R.string.light_mode_pref_key)
        val sharedPref = this?.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        when (sharedPref.getBoolean(lightModeKey, false)) {
            false -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        //basic declarations. UI + ViewModels + RecyclerViews(adapter)
        globalViewModel = ViewModelProvider(this).get(GlobalViewModel::class.java)
        fab = findViewById(R.id.main_activity_fab)

        //Bottom navbar implementation
        bottomNavigation = findViewById(R.id.bottom_navigation_view)
        bottomNavigation.setOnNavigationItemSelectedListener { navButton ->
            val intent = when (navButton.itemId) {
                R.id.nav_settings -> Intent(this.baseContext, SettingsActivity::class.java)
                else -> { null; val s = Snackbar.make(bottomNavigation, "Currently on the Main Screen!", Snackbar.LENGTH_SHORT); s.setAction("Dismiss") { s.dismiss() }; s.show()}  //do nothing as current setting is MainActivity
            }
            try { startActivity(intent as Intent?) } catch (e: Exception) { Log.i("Exception", "Exception: $e occurred") }
            overridePendingTransition(R.anim.fadein, R.anim.fadeout)
            true
        }


        //Context Menu
        contextMenuText = findViewById(R.id.context_menu_sort_by_text)
        registerForContextMenu(contextMenuText)

        //Create broadcast listener and register the filters
       broadcastReceiver = object: BroadcastReceiver() {
           override fun onReceive(context: Context?, intent: Intent?) {
               //get deck, if deck != null then update the checkmark response
               //this is called when adding a new Deck after hiding one for some reason. WHYYY
               if (intent?.action == DeckRecyclerAdapter.changeCompletedForDeck) {
                   val deck = intent?.extras?.getParcelable<Deck>(DeckRecyclerAdapter.changeCompletedForDeckItemID)
                   deck?.let { deck ->
                       globalViewModel.update(deck)
                   }
               }
           }
       }
        val filter = IntentFilter(DeckRecyclerAdapter.changeCompletedForDeck)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)

        //Define adapter for recycler view
        recycler = findViewById(R.id.main_activity_reclyer_view)
        adapter = DeckRecyclerAdapter(this)


        //start application is sorting alpha ascending mode
        globalViewModel.sortBy(Sort.ALPHA_ASC)

        globalViewModel.allDecks.observeForever { deck ->
            deck?.let { adapter.setDecks(deck) }
        }

        //Set RecyclerView.
        recycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        fab.setOnClickListener {
            startActivityForResult(
                Intent(this, AddDeckActivity::class.java),
                AddDeckActivityResultCode
            )
        }

        //Choose sorting type => opens context menu
        contextMenuText.setOnClickListener { view ->
            openContextMenu(view)
        }

    }


    //sort By Context Menu
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.setHeaderTitle("Choose your option")
        menuInflater.inflate(R.menu.sort_by_deck, menu)
    }

    //What happens when button is clicked. Link to globalViewModel.
    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_by_alpha_asc -> {
                globalViewModel.sortBy(Sort.ALPHA_ASC); contextMenuText.setText(R.string.sort_by_alpha_asc); return true; }
            R.id.sort_by_alpha_desc -> {
                globalViewModel.sortBy(Sort.ALPHA_DES); contextMenuText.setText(R.string.sort_by_alpha_des); return true; }
            R.id.sort_by_completed_hidden -> {
                globalViewModel.sortBy(Sort.NON_COM); contextMenuText.setText(R.string.sort_by_non_complete); return true; }
            R.id.sort_by_due_date -> {
                globalViewModel.sortBy(Sort.DUE_DATE); contextMenuText.setText(R.string.sort_by_due_date); return true; }
            else -> return super.onContextItemSelected(item)
        }
    }

    //Dealing /w Adding decks
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //FAB LISTENER
        if (requestCode == AddDeckActivityResultCode && resultCode == Activity.RESULT_OK) {
            //get Deck from FAB button and insert the new Deck into the ViewModel list
            data?.extras?.getParcelable<Deck>(AddDeckActivity.ADD_DECK_REPLY)?.let { deck ->
                globalViewModel.insert(deck)
            }
            //UPDATING A DECK FROM RECYCLER ITEM LISTENER
        } else if (requestCode == DeckRecyclerAdapter.AddDeckActivityStartForResult && resultCode == Activity.RESULT_OK) {
            //Action sent from longItemClick recycler Item.
            //If position and deck is passed, update the delegated position with modified Deck.
            val deck = data?.extras?.getParcelable<Deck>(AddDeckActivity.ADD_DECK_REPLY)

            if (deck?.title == "deleted_object") {
                globalViewModel.delete(deck)
            } else {
                //Update deck
                deck?.let {
                    globalViewModel.update(deck)
                    Log.i("hello", "${deck.date}")
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START) else super.onBackPressed()


    }
    //Destroy the BroadcastReceiver
    override fun onDestroy() {
        Log.i("wow", "I am called (THE DESTROYER)")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        Log.i("wow", "onNaviItemSelected")
        when (item.itemId) {
            R.id.drawer_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            else -> null //do nothing
        }
        return true
    }
}




