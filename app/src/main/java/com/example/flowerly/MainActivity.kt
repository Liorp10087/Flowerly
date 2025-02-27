package com.example.flowerly

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
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
