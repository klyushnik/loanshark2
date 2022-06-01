package studio.sanguine.loanshark2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import studio.sanguine.loanshark2.databinding.ActivityMainBinding

//todo: add entity diagram, ER diagram, user journey to presentation


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var navController : NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar : Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.elevation = 30.0F



        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                //R.id.menu_newRecord, R.id.menu_search, R.id.menu_sort, R.id.navigation_history
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        val ops = (navHostFragment?.childFragmentManager?.fragments?.get(0) as FragmentOperations)

        return when(item.itemId){
            R.id.menu_newRecord -> {
                ops.newItem()
                true
            }
            R.id.menu_newPerson -> {

                ops.newItem()
                true
            }
            R.id.menu_search -> {
                ops.searchBar()
                true
            }
            R.id.menu_sort_asc -> {
                ops.sortAsc()
                true
            }
            R.id.menu_sort_desc -> {
                ops.sortDesc()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }



    val addRecord = fun(){
        val intent = Intent(this, AddModifyDebtActivity::class.java)
        startActivity(intent)
    }


}