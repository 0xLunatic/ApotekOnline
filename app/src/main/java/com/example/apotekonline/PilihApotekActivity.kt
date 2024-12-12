package com.example.apotekonline

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

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

            // Mengambil data dari intent sebelumnya, misalnya nama pemesan, alamat, dll
            val namaPemesan = intent.getStringExtra("NAMA_PEMESAN") ?: ""
            val alamatPengiriman = intent.getStringExtra("ALAMAT_PENGIRIMAN") ?: ""
            val nomorTelepon = intent.getStringExtra("NOMOR_TELEPON") ?: ""
            val totalPrice = intent.getStringExtra("TOTAL_PRICE") ?: ""

            // Kirim data ke KonfirmasiPembayaranActivity
            val intent = Intent(this, KonfirmasiPembayaranActivity::class.java).apply {
                putExtra("NAMA_PEMESAN", namaPemesan)
                putExtra("ALAMAT_PENGIRIMAN", alamatPengiriman)
                putExtra("NOMOR_TELEPON", nomorTelepon)
                putExtra("TOTAL_PRICE", totalPrice)
                putExtra("SELECTED_APOTEK", selectedApotek) // Mengirim apotek yang dipilih
            }
            startActivity(intent) // Arahkan ke KonfirmasiPembayaranActivity
            finish() // Tutup PilihApotekActivity
        }
    }
}
