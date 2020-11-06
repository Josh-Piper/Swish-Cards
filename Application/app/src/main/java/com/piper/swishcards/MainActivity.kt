package com.piper.swishcards

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    //Declare the UI widgets.
    private lateinit var globalViewModel: MainActivityViewModel
    private lateinit var contextMenuText: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: DeckRecyclerAdapter
    private lateinit var broadcastReceiver: BroadcastReceiver
    private lateinit var drawer: DrawerLayout
    private lateinit var topBarNav: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var firstFragment: BottomBarFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Topbar navigational drawer
        drawer = findViewById(R.id.drawer)
        topBarNav = findViewById(R.id.topbar_nav)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        //Navigation drawer logic
        //Set the activity to use its overridden function.
        //Finding a way to simplify this is prefferred, i.e. through fragments (but issue with taking the whole screen [half])
        topBarNav.bringToFront()
        val toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigational_drawer_open, R.string.navigational_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        topBarNav.setNavigationItemSelectedListener(this)

        //Inflate bottom navigational bar
        firstFragment = BottomBarFragment.get().apply {
            setScreen(SCREEN.MainPage)
        }

        supportFragmentManager.beginTransaction()
        .add(R.id.fragment_container_bottom_bar, firstFragment)
        .commit()

        ////////////////////
        //Get current colour setting (collected from Settings Activity)
        val lightModeKey = getString(R.string.light_mode_pref_key)
        val sharedPref = this.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        //Set colour scheme to match SettingsActivity
        SettingsActivity.updateColourScheme(sharedPref.getBoolean(lightModeKey, false))

        //basic declarations. UI + ViewModels + RecyclerViews(adapter)
        globalViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        fab = findViewById(R.id.main_activity_fab)

        //Context Menu
        //Registers the sorting menu.
        contextMenuText = findViewById(R.id.context_menu_sort_by_text)
        registerForContextMenu(contextMenuText)

        //Create broadcast listener and register the filters
        //A Local Broadcast Listener isn't required as a callback function would be more appropriate
        //used for learning purposes. Other scenaro's use Callbacks...
        //For example, a Tablet may have better drawing applications, thus, may reuse the Broadcaster to
        //support for other things. (voice, video, drawing...)
       broadcastReceiver = object: BroadcastReceiver() {
           override fun onReceive(context: Context?, intent: Intent?) {

               //Sent from the DeckRecyclerView for updating checkbox listener (when updating whether completed or not)
               //Everything regarding editing/adding Decks is done at MainActivity. Used for centralisation
               if (intent?.action == DeckRecyclerAdapter.changeCompletedForDeck) {
                   val deck = intent.extras?.getParcelable<Deck>(DeckRecyclerAdapter.changeCompletedForDeckItemID)
                   deck?.let { deck ->
                       globalViewModel.update(deck)
                   }
               }
           }
       }
        //Register the LocalBroadcast Listener. Lets the above code activated when a listen is activated (i.e. listening for DeckRecycler calls)
        val filter = IntentFilter(DeckRecyclerAdapter.changeCompletedForDeck)
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter)

        //Define adapter for recycler view
        recycler = findViewById(R.id.main_activity_reclyer_view)
        adapter = DeckRecyclerAdapter(this)

        //start application is sorting alphabetically ascending mode
        globalViewModel.sortBy(Sort.ALPHA_ASC)

        //Continually observe the changes made to the LiveData. Update the Recycler View when the ROOM database is updated.
        globalViewModel.allDecks.observeForever { deck ->
            deck?.let { adapter.setDecks(deck) }
        }

        //Set RecyclerView settings
        recycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        //Floating Action Bar on Click Listener
        fab.setOnClickListener {
            startActivityForResult(
                Intent(this, AddDeckActivity::class.java),
                AddDeckActivityRequestCode
            )
        }

        //Choose sorting type => opens context menu
        contextMenuText.setOnClickListener { view ->
            openContextMenu(view)
        }
        //Debugging
        firstFragment.showCurrent()
    }

    //sort By Context Menu,
    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.setHeaderTitle("Choose your option")
        menuInflater.inflate(R.menu.sort_by_deck, menu)
    }

    ////////////////////////////////////////////////////
    //IMPORTANT: Sorting context menu options. Will change the ViewModels sorting option.
    ////////////////////////////////////////////////////
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_by_alpha_asc -> {
                globalViewModel.sortBy(Sort.ALPHA_ASC); contextMenuText.setText(R.string.sort_by_alpha_asc); true; }
            R.id.sort_by_alpha_desc -> {
                globalViewModel.sortBy(Sort.ALPHA_DES); contextMenuText.setText(R.string.sort_by_alpha_des); true; }
            R.id.sort_by_completed_hidden -> {
                globalViewModel.sortBy(Sort.NON_COM); contextMenuText.setText(R.string.sort_by_non_complete); true; }
            R.id.sort_by_due_date -> {
                globalViewModel.sortBy(Sort.DUE_DATE); contextMenuText.setText(R.string.sort_by_due_date); return true; }
            else -> super.onContextItemSelected(item)
        }
    }

    //Listener for the FAB and recycler view events.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        //FAB LISTENER
        if (requestCode == AddDeckActivityRequestCode && resultCode == Activity.RESULT_OK) {
            //get Deck from FAB button and insert the new Deck into the ViewModel list
            data?.extras?.getParcelable<Deck>(AddDeckActivity.ADD_DECK_REPLY)?.let { deck ->
                globalViewModel.insert(deck)
            }
            //UPDATING A DECK FROM RECYCLER ITEM ON CLICK LISTENER
        } else if (requestCode == DeckRecyclerAdapter.AddDeckActivityStartForResult && resultCode == Activity.RESULT_OK) {
            //Action sent from longItemClick recycler Item.
            val deck = data?.extras?.getParcelable<Deck>(AddDeckActivity.ADD_DECK_REPLY)

            if (deck?.title == "deleted_object") { //Deleting based on this string is not a bullet proof method, may set with a new specific UUID and delete per id
                globalViewModel.delete(deck)
            } else {
                //Update deck
                deck?.let {
                    globalViewModel.update(deck)
                }
            }
        }
    }

    //If the drawer is open and the application's back button is pressed, close the drawer, NOT the application.
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) { drawer.closeDrawer(GravityCompat.START) } else {
            firstFragment.closeScreen()
            super.onBackPressed()
        }
    }

    //Destroy the BroadcastReceiver, necessary to prevent memory leaks
    override fun onDestroy() {
        Log.i(MainActivity.GlobalLoggingName, "MainActivity onDestroy() called")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver)
        super.onDestroy()
    }

    //Navigation Drawer options. Relies on Bottom Bar fragment, used for central administration.
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        firstFragment.changeLocationFromDrawer(item.itemId)
        return true
    }

    companion object {
        //FAB to AddDeck
        const val AddDeckActivityRequestCode = 25
        const val GlobalLoggingName = "josh.pipers.super.secret.logging"
    }
}




