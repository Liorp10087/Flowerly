package com.example.flowerly

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.flowerly.databinding.ActivityMainBinding
import com.example.flowerly.viewmodel.UserViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.refreshAllUsers()

        val navController = (supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as? NavHostFragment)?.navController
        val bottomNavigation = binding.bottomNavigation

        userViewModel.currentUser.observe(this) { currentUser ->
            bottomNavigation.visibility = if (currentUser == null) View.GONE else View.VISIBLE
            if (currentUser == null) {
                navController?.navigate(R.id.loginFragment)
            }

            binding.fabAdd.visibility = if (currentUser == null) View.GONE else View.VISIBLE
        }

        navController?.let { controller ->
            bottomNavigation.setupWithNavController(controller)

            bottomNavigation.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.nav_home -> {
                        controller.navigate(R.id.mainFragment)
                        true
                    }
                    R.id.nav_profile -> {
                        controller.navigate(R.id.profileFragment)
                        true
                    }
                    else -> false
                }
            }
        }

        binding.fabAdd.setOnClickListener {
            navController?.navigate(R.id.uploadPostFragment)
        }
    }
}
