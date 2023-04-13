package com.example.postapp.postapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.example.postapp.R
import com.example.postapp.databinding.ActivityPostappBinding
import com.google.gson.annotations.SerializedName


const val KEY_EXTRA_ID_POST = "KEY_EXTRA_ID_POST"
const val KEY_EXTRA_USERID_POST = "KEY_EXTRA_USERID_POST"
const val KEY_EXTRA_TITLE_POST = "KEY_EXTRA_TITLE_POST"
const val KEY_EXTRA_BODY_POST = "KEY_EXTRA_BODY_POST"
const val KEY_EXTRA_ISFAVORITE_POST = "KEY_EXTRA_ISFAVORITE_POST"


const val PREFERENCES_KEY = "com.example.postapp"
const val STATE_INITIALIZED = "STATE_INITIALIZED"


class PostAppActivity: AppCompatActivity() {

    lateinit var navController: NavController
    private lateinit var binding: ActivityPostappBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPostappBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_postactivity) as NavHostFragment
        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}