package com.example.apotekonline

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.authentication.LoginActivity

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
        Log.d(TAG, "onCreate: Activity Created")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: Activity Started")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Activity Resumed")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: Activity Paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: Activity Stopped")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart: Activity Restarted")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: Activity Destroyed")
    }
}