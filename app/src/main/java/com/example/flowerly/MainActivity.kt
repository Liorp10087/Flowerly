// MainActivity.kt
package com.example.flowerly

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.flowerly.databinding.ActivityMainBinding
import com.example.flowerly.viewmodel.AuthViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment
        val navController = navHostFragment?.navController

        authViewModel.user.observe(this) { user ->
            if (user == null) {
                navController?.navigate(R.id.loginFragment)
            }
        }

        if (navController != null) {
            val bottomNavigation: BottomNavigationView = binding.bottomNavigation
            bottomNavigation.setupWithNavController(navController)

            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        navController.navigate(R.id.mainFragment)
                        true
                    }
                    R.id.nav_profile -> {
                        navController.navigate(R.id.profileFragment)
                        true
                    }
                    else -> false
                }
            }
        }
    }
}