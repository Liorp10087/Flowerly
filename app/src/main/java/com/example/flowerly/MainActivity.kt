package com.example.flowerly

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics

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

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val postList = listOf(
            Post(R.drawable.rose1, "Rose", "A beautiful red rose"),
            Post(R.drawable.tulip, "Tulip", "Bright and colorful tulips"),
            Post(R.drawable.rose2, "Sunflower", "Sunflowers follow the sun"),
            Post(R.drawable.dandelion, "Sunflower", "Sunflowers follow the sun")

        )

        recyclerView.adapter = PostAdapter(postList)
    }
}
