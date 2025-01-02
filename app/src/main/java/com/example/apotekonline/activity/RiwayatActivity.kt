package com.example.apotekonline.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.apotekonline.R
import com.example.apotekonline.constructor.Pembayaran
import com.example.apotekonline.constructor.PembayaranDB

class RiwayatActivity : AppCompatActivity() {

    private lateinit var pembayaranDB: PembayaranDB
    private lateinit var containerRiwayat: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.riwayat)

        pembayaranDB = PembayaranDB(this)
        containerRiwayat = findViewById(R.id.containerRiwayat)

        val listPembayaran = pembayaranDB.getAllPembayaran()
        displayRiwayatPembelian(listPembayaran)

        val backButton: ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    private fun displayRiwayatPembelian(listPembayaran: List<Pembayaran>) {
        containerRiwayat.removeAllViews()

        for (pembayaran in listPembayaran) {
            val cardView = layoutInflater.inflate(R.layout.item_pembayaran, containerRiwayat, false) as CardView

            val tvNamaPemesan = cardView.findViewById<TextView>(R.id.tvNamaPemesan)
            val tvAlamatPengiriman = cardView.findViewById<TextView>(R.id.tvAlamatPengiriman)
            val tvMetodePembayaran = cardView.findViewById<TextView>(R.id.tvMetodePembayaran)
            val tvNomorTelepon = cardView.findViewById<TextView>(R.id.tvNomorTelepon)
            val tvTanggal = cardView.findViewById<TextView>(R.id.tvTanggal)
            val tvTotalHarga = cardView.findViewById<TextView>(R.id.tvTotalHarga)

            tvNamaPemesan.text = "Nama Pemesan: ${pembayaran.namaPemesan}"
            tvAlamatPengiriman.text = "Alamat Pengiriman: ${pembayaran.alamatPengiriman}"
            tvMetodePembayaran.text = "Metode Pembayaran: ${pembayaran.metodePembayaran}"
            tvNomorTelepon.text = "Nomor Telepon: ${pembayaran.nomorTelepon}"
            tvTanggal.text = "Tanggal: ${pembayaran.tanggal}"
            tvTotalHarga.text = "Total Harga: Rp ${pembayaran.totalHarga}"

            containerRiwayat.addView(cardView)
        }
    }
}
