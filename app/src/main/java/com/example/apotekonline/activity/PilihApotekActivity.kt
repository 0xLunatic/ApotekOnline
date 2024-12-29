package com.example.apotekonline.activity

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R

class PilihApotekActivity : AppCompatActivity() {
    private lateinit var apotekListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pilih_apotek)

        apotekListView = findViewById(R.id.apotekListView)

        // Data dummy untuk apotek, ganti dengan data yang lebih dinamis
        val apotekList = listOf("Apotek A", "Apotek B", "Apotek C")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, apotekList)
        apotekListView.adapter = adapter

        apotekListView.setOnItemClickListener { _, _, position, _ ->
            val selectedApotek = apotekList[position]

            // Pastikan data diterima dengan benar
            val namaPemesan = intent.getStringExtra("NAMA_PEMESAN") ?: ""
            val alamatPengiriman = intent.getStringExtra("ALAMAT_PENGIRIMAN") ?: ""
            val nomorTelepon = intent.getStringExtra("NOMOR_TELEPON") ?: ""

            // Kembalikan hasil ke PembayaranActivity
            val resultIntent = Intent()
            resultIntent.putExtra("SELECTED_APOTEK", selectedApotek)
            resultIntent.putExtra("NAMA_PEMESAN", namaPemesan)
            resultIntent.putExtra("NOMOR_TELEPON", nomorTelepon)
            resultIntent.putExtra("ALAMAT_PENGIRIMAN", alamatPengiriman)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}
