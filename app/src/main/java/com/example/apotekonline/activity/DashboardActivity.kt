package com.example.apotekonline.activity

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R
import com.example.apotekonline.authentication.LoginActivity
import com.example.apotekonline.mapslistener.MapsActivity

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val usernameText: TextView = findViewById(R.id.welcomeText)

        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "User")

        usernameText.text = "Welcome, $username!"

        // Inisialisasi ImageButton cariApotek
        val imageButtonGmaps: ImageButton = findViewById(R.id.cariApotek)
        imageButtonGmaps.setOnClickListener {
            val mapIntent = Intent(this, MapsActivity::class.java)
            startActivity(mapIntent)
        }

        // Inisialisasi ImageButton untuk gambar pesan
        val imageButtonPesan: ImageButton = findViewById(R.id.pesan)
        imageButtonPesan.setOnClickListener {
            // Intent untuk berpindah ke MenuPesanActivity
            val pesanIntent = Intent(this, MenuPesanActivity::class.java)
            startActivity(pesanIntent)
        }

        // Inisialisasi ImageButton untuk gambar riwayat
        val imageButtonRiwayat: ImageButton = findViewById(R.id.riwayat)
        imageButtonRiwayat.setOnClickListener {
            // Intent untuk berpindah ke RiwayatActivity
            val riwayatIntent = Intent(this, RiwayatActivity::class.java)
            startActivity(riwayatIntent)
        }

        // Inisialisasi ImageButton untuk gambar profil
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            // Create a PopupMenu to show options
            val popupMenu = android.widget.PopupMenu(this, profileButton)
            menuInflater.inflate(R.menu.profile_menu, popupMenu.menu) // Inflate the custom menu

            // Set the menu item click listener
            popupMenu.setOnMenuItemClickListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.menu_profile -> {
                        // Navigate to ProfileActivity
                        //val profileIntent = Intent(this, ProfileActivity::class.java)
                        //startActivity(profileIntent)
                        true
                    }
                    R.id.menu_logout -> {
                        // Handle logout (e.g., clear user data or log out)
                        val logoutIntent = Intent(this, LoginActivity::class.java)
                        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        sharedPreferences.edit().clear().apply()
                        startActivity(logoutIntent)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }
}
