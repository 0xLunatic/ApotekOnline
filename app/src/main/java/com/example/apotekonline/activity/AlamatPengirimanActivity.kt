package com.example.apotekonline.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R

class AlamatPengirimanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alamat_pengiriman_activity)

        val alamatPengirimanEditText = findViewById<EditText>(R.id.alamatPengirimanEditText)
        val lanjutkanButton = findViewById<Button>(R.id.lanjutkanButton)

        lanjutkanButton.setOnClickListener {
            val alamatPengiriman = alamatPengirimanEditText.text.toString().trim()

            if (alamatPengiriman.isEmpty()) {
                Toast.makeText(this, "Harap lengkapi alamat pengiriman.", Toast.LENGTH_SHORT).show()
            } else {
                // Mengambil data yang sebelumnya dikirimkan, misalnya nama dan nomor telepon
                val namaPemesan = intent.getStringExtra("NAMA_PEMESAN") ?: ""
                val nomorTelepon = intent.getStringExtra("NOMOR_TELEPON") ?: ""
                val totalPrice = intent.getStringExtra("TOTAL_PRICE") ?: ""

                // Kirim data ke KonfirmasiPembayaranActivity
                val intent = Intent(this, KonfirmasiPembayaranActivity::class.java).apply {
                    putExtra("NAMA_PEMESAN", namaPemesan)
                    putExtra("ALAMAT_PENGIRIMAN", alamatPengiriman)
                    putExtra("NOMOR_TELEPON", nomorTelepon)
                    putExtra("TOTAL_PRICE", totalPrice)
                }
                startActivity(intent) // Arahkan ke KonfirmasiPembayaranActivity
                finish() // Tutup AlamatPengirimanActivity
            }
        }
    }
}
