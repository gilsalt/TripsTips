package com.tripstips.app.view.activities

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.tripstips.app.R
import com.tripstips.app.databinding.ActivityMainBinding
import com.tripstips.app.viewmodel.AuthViewModel

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        setUpToolbar()
        setUpNavController()

        binding.fabAddPost.setOnClickListener {
           navController.navigate(R.id.newPostFragment)
        }
    }

    private fun setUpToolbar(){
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.white))

    }

    private fun setUpNavController(){

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        setupActionBarWithNavController(navController, AppBarConfiguration(navController.graph))
        binding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id != R.id.exploreFragment){
                supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
            }
            else{
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            }
            when (destination.id) {
                R.id.startFragment,R.id.signInFragment, R.id.signUpFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.appBar.visibility = View.GONE
                    binding.fabAddPost.visibility = View.GONE
                }
                else -> {
                    authViewModel.currentUser.observe(this) { user ->
                        if (user != null) {
                            loggedUser = user
                        }
                    }
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.appBar.visibility = View.VISIBLE
                    binding.fabAddPost.visibility = View.VISIBLE
                }
            }
        }

        authViewModel.checkUserSession()
        authViewModel.currentUser.observe(this@MainActivity) { user ->
            user?.let {
                val startDestination = if (user.userId.isEmpty()) R.id.startFragment else R.id.exploreFragment
                val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)
                navGraph.setStartDestination(startDestination)
                navController.graph = navGraph
            }
        }


    }

    fun updateToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}