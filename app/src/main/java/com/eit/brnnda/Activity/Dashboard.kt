package com.eit.brnnda.Activity

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.eit.brnnda.R
import com.eit.brnnda.view_model.MyViewModel

class Dashboard : AppCompatActivity() {

    private lateinit var viewModel: MyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val navView: BottomNavigationView = findViewById(R.id.nav_view_bottom)
        viewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        viewModel.cartItemCountData.observe(this, Observer {

            navView.getOrCreateBadge(R.id.bottom_cart).number = it

        })


        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.bottom_wish,
                R.id.bottom_cart,
                R.id.bottom_account
            )
        )

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.productFragment -> {
                    navView.visibility = View.GONE
                }
                R.id.catFragment -> {

                    navView.visibility = View.GONE
                }
                R.id.orderHistoryFragment -> {
                    navView.visibility = View.GONE
                }
                R.id.orderTrackFragment -> {
                    navView.visibility = View.GONE
                }
                R.id.searchFragment -> {
                    navView.visibility = View.GONE
                }


                R.id.updateProfileFragment -> {
                    navView.visibility = View.GONE

                }

                R.id.catWiseProductFragment -> {
                    navView.visibility = View.GONE

                }
                R.id.checkOutFragment -> {
                    navView.visibility = View.GONE
                }
                else -> {
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                }
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}