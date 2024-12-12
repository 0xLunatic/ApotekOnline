package com.example.apotekonline

import KeranjangDB
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class KonfirmasiPembayaranActivity : AppCompatActivity() {

    private lateinit var namaPemesan: String
    private lateinit var alamatPengiriman: String
    private lateinit var nomorTelepon: String
    private lateinit var totalPrice: String
    private lateinit var currentDate: String
    private lateinit var keranjangDB: KeranjangDB
    private lateinit var pembayaranDB: PembayaranDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.konfirmasi_pembayaran)

        val namaPemesanTextView = findViewById<TextView>(R.id.namaPemesanTextView)
        val alamatPengirimanTextView = findViewById<TextView>(R.id.alamatPengirimanTextView)
        val nomorTeleponTextView = findViewById<TextView>(R.id.nomorTeleponTextView)
        val totalPriceTextView = findViewById<TextView>(R.id.totalPriceTextView)
        val currentDateTextView = findViewById<TextView>(R.id.currentDateTextView)
        val cartItemsContainer = findViewById<LinearLayout>(R.id.cartItemsContainer)
        val konfirmasiPembayaranButton = findViewById<Button>(R.id.konfirmasiPembayaranButton)

        // Initialize databases
        keranjangDB = KeranjangDB(this)
        pembayaranDB = PembayaranDB(this)

        // Mendapatkan data yang dikirimkan
        val intent = intent
        namaPemesan = intent.getStringExtra("NAMA_PEMESAN").toString()
        alamatPengiriman = intent.getStringExtra("ALAMAT_PENGIRIMAN").toString()
        nomorTelepon = intent.getStringExtra("NOMOR_TELEPON").toString()
        totalPrice = intent.getStringExtra("TOTAL_PRICE").toString()

        // Format tanggal saat ini
        val currentDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        currentDate = currentDateFormat.format(Date())

        // Menampilkan data
        namaPemesanTextView.text = "Nama Pemesan: $namaPemesan"
        alamatPengirimanTextView.text = "Alamat Pengiriman: $alamatPengiriman"
        nomorTeleponTextView.text = "Nomor Telepon: $nomorTelepon"
        totalPriceTextView.text = "Total Harga: $totalPrice"
        currentDateTextView.text = "Tanggal: $currentDate"

        // Mengambil semua item dari keranjang
        val cartItems = keranjangDB.getAllCartItems()

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong. Silakan tambahkan item.", Toast.LENGTH_LONG)
                .show()
            finish()  // Kembali jika keranjang kosong
            return
        }

        // Menampilkan semua item di UI
        displayCartItems(cartItems)

        // Konfirmasi Pembayaran
        konfirmasiPembayaranButton.setOnClickListener {
            // Simpan data ke DB dan konfirmasi pembayaran
            saveToDatabase(cartItems)
            Toast.makeText(this, "Pembayaran berhasil!", Toast.LENGTH_SHORT).show()
            finish()  // Kembali ke halaman utama
        }
    }

    private fun displayCartItems(cartItems: List<Product>) {
        val cartItemsContainer = findViewById<LinearLayout>(R.id.cartItemsContainer)

        // Menampilkan item dalam keranjang
        for (item in cartItems) {
            val itemTextView = TextView(this)
            itemTextView.text = "${item.name} - ${item.price}"
            cartItemsContainer.addView(itemTextView)
        }
    }

    private fun saveToDatabase(cartItems: List<Product>) {
        // Logika untuk menyimpan data ke dalam database PembayaranDB
        val result = pembayaranDB.insertPembayaran(
            namaPemesan,
            alamatPengiriman,
            nomorTelepon,
            totalPrice.toInt(),  // pastikan totalPrice adalah tipe data yang benar
            currentDate,
            cartItems.size
        )

        if (result != -1L) {
            // Pembayaran berhasil disimpan
            // Mengosongkan keranjang setelah pembayaran berhasil
            // keranjangDB.clearCart()   Implementasikan metode ini untuk menghapus semua item dari keranjang
        } else {
            Toast.makeText(this, "Gagal menyimpan data pembayaran.", Toast.LENGTH_SHORT).show()
        }
    }
}
