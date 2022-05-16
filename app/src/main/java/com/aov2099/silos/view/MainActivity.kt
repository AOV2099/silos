package com.aov2099.silos.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import com.aov2099.silos.R
import com.aov2099.silos.databinding.ActivityAuthActivityBinding
import com.aov2099.silos.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fb: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUp()
        getData()
        onClick()
    }

    private fun setUp() {
        supportActionBar?.title = "SILOS"
    }

    private fun getData() {
        fb = FirebaseDatabase.getInstance().getReference("silo")
        fb.get().addOnSuccessListener {

            if (it.exists()){

                val image = it.child("image").value.toString()
                val product = it.child("product").value.toString()
                val measured = it.child("measured").value.toString()
                val original = it.child("original").value.toString()

                Picasso.get().load(image).into(binding.ivImage)
                binding.tvTitle.text = product
                binding.tvMeasured.text = measured
                binding.tvOriginal.text = original

            }else{
                Toast.makeText(this, "Data doesn't exist", Toast.LENGTH_SHORT).show()
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Error Reading Data", Toast.LENGTH_SHORT).show()
        }
    }

    private fun onClick() {

        binding.swipeRefresh.setOnRefreshListener {
            getData()
            binding.swipeRefresh.isRefreshing = false
        }

    }
}