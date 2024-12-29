package com.example.apotekonline.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R
import com.example.apotekonline.payment.TransferActivity

class KonfirmasiPembayaranActivity : AppCompatActivity() {

    private lateinit var namaPemesanTextView: TextView
    private lateinit var alamatPengirimanTextView: TextView
    private lateinit var nomorTeleponTextView: TextView
    private lateinit var currentDateTextView: TextView
    private lateinit var totalPriceTextView: TextView
    private lateinit var konfirmasiPembayaranButton: Button
    private lateinit var ubahDataPembeliButton: Button
    private lateinit var ubahAlamatButton: Button
    private lateinit var metodePembayaranTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.konfirmasi_pembayaran)

        // Initialize the UI elements
        namaPemesanTextView = findViewById(R.id.namaPemesanTextView)
        alamatPengirimanTextView = findViewById(R.id.alamatPengirimanTextView)
        nomorTeleponTextView = findViewById(R.id.nomorTeleponTextView)
        currentDateTextView = findViewById(R.id.currentDateTextView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        konfirmasiPembayaranButton = findViewById(R.id.konfirmasiPembayaranButton)
        ubahDataPembeliButton = findViewById(R.id.ubahDataPembeliButton)
        ubahAlamatButton = findViewById(R.id.ubahAlamatButton)
        metodePembayaranTextView = findViewById(R.id.metodePembayaranTextView)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val namaPemesan = sharedPreferences.getString("namaPemesan", "Unknown")
        val alamatPengiriman = sharedPreferences.getString("alamatPengiriman", "Unknown")
        val metodePembayaran = sharedPreferences.getString("metodePembayarna", "Unknown")
        val nomorTelepon = sharedPreferences.getString("nomorTelepon", "Unknown")
        val currentDate = sharedPreferences.getString("currentDate", "Unknown")
        val totalPrice = sharedPreferences.getInt("totalPrice", 0)

        namaPemesanTextView.text = "Nama Pemesan: $namaPemesan"
        metodePembayaranTextView.text = "Metode Pembayaran: $metodePembayaran"
        alamatPengirimanTextView.text = "Alamat Pengiriman: $alamatPengiriman"
        nomorTeleponTextView.text = "Nomor Telepon: $nomorTelepon"
        currentDateTextView.text = "Tanggal: $currentDate"
        totalPriceTextView.text = "Total Harga: Rp. $totalPrice"

        // Set listeners for the buttons
        konfirmasiPembayaranButton.setOnClickListener {
            if (metodePembayaranTextView.toString().contains("Transfer Bank")){
                val intent = Intent(this, TransferActivity::class.java)
                intent.putExtra("TRANSFER_METHOD", "Transfer Bank")
                intent.putExtra("TRANSFER_NAME", "APOTEK ONLINE")
                intent.putExtra("TRANSFER_NUMBER", "7231 7823 8392")
                startActivity(intent)
            }else if(metodePembayaranTextView.toString().contains("GoPay")){
                val intent = Intent(this, TransferActivity::class.java)
                intent.putExtra("TRANSFER_METHOD", "GoPay")
                intent.putExtra("TRANSFER_NAME", "APOTEK ONLINE")
                intent.putExtra("TRANSFER_NUMBER", "0821 8383 8127")
                startActivity(intent)
            }else if(metodePembayaranTextView.toString().contains("LinkAja")){
                val intent = Intent(this, TransferActivity::class.java)
                intent.putExtra("TRANSFER_METHOD", "LinkAja")
                intent.putExtra("TRANSFER_NAME", "APOTEK ONLINE")
                intent.putExtra("TRANSFER_NUMBER", "0821 8383 8127")
                startActivity(intent)
            }else if(metodePembayaranTextView.toString().contains("ShopeePay")){
                val intent = Intent(this, TransferActivity::class.java)
                intent.putExtra("TRANSFER_METHOD", "ShopeePay")
                intent.putExtra("TRANSFER_NAME", "APOTEK ONLINE")
                intent.putExtra("TRANSFER_NUMBER", "0821 8383 8127")
                startActivity(intent)
            }else if(metodePembayaranTextView.toString().contains("OVO")){
                val intent = Intent(this, TransferActivity::class.java)
                intent.putExtra("TRANSFER_METHOD", "OVO")
                intent.putExtra("TRANSFER_NAME", "APOTEK ONLINE")
                intent.putExtra("TRANSFER_NUMBER", "0821 8383 8127")
                startActivity(intent)
            }else if(metodePembayaranTextView.toString().contains("CASH")){

            }
        }

        ubahDataPembeliButton.setOnClickListener {
            // Handle the change of buyer data
            val intent = Intent(this, PembayaranActivity::class.java)
            startActivity(intent)
        }

        ubahAlamatButton.setOnClickListener {
            // Handle the change of address
            val intent = Intent(this, PembayaranActivity::class.java)
            startActivity(intent)
        }
    }
}
