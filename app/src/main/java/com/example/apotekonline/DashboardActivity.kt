package com.example.apotekonline

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R
import com.example.apotekonline.mapslistener.MapsActivity

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard) // Pastikan layout yang benar

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
    }
}
