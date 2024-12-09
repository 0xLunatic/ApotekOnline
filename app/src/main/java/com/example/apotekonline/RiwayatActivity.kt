package com.example.apotekonline

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class RiwayatActivity : AppCompatActivity() {

    private lateinit var pembayaranDB: PembayaranDB
    private lateinit var containerRiwayat: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.riwayat)

        // Inisialisasi UI dan Database
        pembayaranDB = PembayaranDB(this)
        containerRiwayat = findViewById(R.id.containerRiwayat)

        // Mengambil data pembelian dari database
        val listPembelian = pembayaranDB.getAllPembayaran()

        // Menampilkan setiap item pembelian
        displayRiwayatPembelian(listPembelian)
    }

    private fun displayRiwayatPembelian(listPembelian: List<Pembayaran>) {
        containerRiwayat.removeAllViews()

        for (pembelian in listPembelian) {
            val cardView = layoutInflater.inflate(R.layout.item_pembayaran, containerRiwayat, false) as CardView

            // Inisialisasi view dalam CardView

            val tvNamaObat = cardView.findViewById<TextView>(R.id.cartItemName)
            val tvTotalHarga = cardView.findViewById<TextView>(R.id.cartItemPrice)
            val tvTanggalPembelian = cardView.findViewById<TextView>(R.id.cartItemDate)
            val tvJumlahObat = cardView.findViewById<TextView>(R.id.cartItemQuantity)

            // Set data dari database
            tvNamaObat.text = pembelian.nama // Nama obat
            tvTotalHarga.text = "Rp ${pembelian.totalHarga}" // Total harga
            tvTanggalPembelian.text = "Tanggal: ${pembelian.tanggal}" // Tanggal pembelian
            tvJumlahObat.text = "Jumlah: ${pembelian.jumlah_obat}" // Jumlah obat

            // Tambahkan CardView ke dalam container
            containerRiwayat.addView(cardView)
        }

        val backButton : ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }
}
