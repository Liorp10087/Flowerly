package com.example.flowerly

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, "app_start")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle)

        Log.d("FirebaseTest", "Firebase Analytics Event Logged!")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        val navController = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!.findNavController()

        bottomNavigation.setOnNavigationItemSelectedListener { item ->
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
