package com.example.apotekonline.payment

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.apotekonline.R
import com.example.apotekonline.activity.DashboardActivity
import org.w3c.dom.Text

class TransferActivity : AppCompatActivity() {
    private lateinit var konfirmasiButton : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_transfer)

        konfirmasiButton = findViewById(R.id.konfirmasiButton)

        val transferMethod = intent.getStringExtra("TRANSFER_METHOD")
        val transferName = intent.getStringExtra("TRANSFER_NAME")
        val transferNumber = intent.getStringExtra("TRANSFER_NUMBER")

        findViewById<TextView>(R.id.transferMethodValue).text = transferMethod
        findViewById<TextView>(R.id.transferNameValue).text = transferName
        findViewById<TextView>(R.id.transferVANumberValue).text = transferNumber

        konfirmasiButton.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Berhasil Konfrmasi Pembayaran", Toast.LENGTH_SHORT)
        }

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
