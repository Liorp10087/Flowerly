package com.example.flowerly

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.flowerly.database.AppDatabase
import com.example.flowerly.databinding.ActivityMainBinding
import com.example.flowerly.viewmodel.AuthViewModel
import com.example.flowerly.viewmodel.AuthViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val authViewModel: AuthViewModel by viewModels {
        AuthViewModelFactory(AppDatabase.getDatabase(this).userDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHostFragment?.navController
        val bottomNavigation: BottomNavigationView = binding.bottomNavigation

        authViewModel.user.observe(this) { user ->
            bottomNavigation.visibility = if (user == null) View.GONE else View.VISIBLE
            if (user == null) {
                navController?.navigate(R.id.loginFragment)
            }
        }

        if (authViewModel.user.value == null) {
            navController?.navigate(R.id.loginFragment)
        }

        navController?.let {
            bottomNavigation.setupWithNavController(it)

            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        it.navigate(R.id.mainFragment)
                        true
                    }
                    R.id.nav_profile -> {
                        it.navigate(R.id.profileFragment)
                        true
                    }
                    else -> false
                }
            }
        }

        val fabUpload: FloatingActionButton = findViewById(R.id.fab_add)
        fabUpload.setOnClickListener {
            navController?.navigate(R.id.uploadPostFragment)
        }
    }
}
