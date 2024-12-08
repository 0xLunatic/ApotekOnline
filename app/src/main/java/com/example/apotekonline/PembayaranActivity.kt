package com.example.apotekonline

import KeranjangDB
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PembayaranActivity : AppCompatActivity() {
    private lateinit var pembayaranDB: PembayaranDB
    private lateinit var keranjangDB: KeranjangDB
    private lateinit var konfirmasiButton: Button
    private lateinit var batalButton: Button
    private lateinit var totalPriceTextView: TextView
    private lateinit var itemPembayaranContainer: LinearLayout

    private var totalPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pembayaran)

        // Inisialisasi database
        pembayaranDB = PembayaranDB(this)
        keranjangDB = KeranjangDB(this)

        // Inisialisasi UI
        itemPembayaranContainer = findViewById(R.id.itemPembayaranContainer)
        totalPriceTextView = findViewById(R.id.totalPrice)
        konfirmasiButton = findViewById(R.id.konfirmasiButton)
        batalButton = findViewById(R.id.batalButton)

        // Mengambil semua item dari keranjang
        val cartItems = keranjangDB.getAllCartItems()

        // Menampilkan semua item di UI
        displayCartItems(cartItems)

        // Mengambil tanggal saat ini
        val currentDate = getCurrentDate()

        // Menampilkan tanggal ke dalam TextView
        findViewById<TextView>(R.id.tanggalText).text = currentDate

        // Set listener untuk tombol konfirmasi
        konfirmasiButton.setOnClickListener {
            val namaPemesan = findViewById<EditText>(R.id.namaPemesanEditText).text.toString()
            val alamatPengiriman = findViewById<EditText>(R.id.alamatPengirimanEditText).text.toString()
            val nomorTelepon = findViewById<EditText>(R.id.nomorTeleponEditText).text.toString()
            val tanggal = currentDate // Menggunakan tanggal saat ini

            if (namaPemesan.isNotEmpty() && alamatPengiriman.isNotEmpty() && nomorTelepon.isNotEmpty()) {
                // Anggap jumlah obat sebagai total item di keranjang
                val jumlahObat = cartItems.size // Hitung jumlah total item di keranjang
                val result = pembayaranDB.insertPembayaran(namaPemesan, alamatPengiriman, nomorTelepon, totalPrice, tanggal, jumlahObat)
                if (result != -1L) {
                    Toast.makeText(this, "Pembayaran berhasil untuk $namaPemesan", Toast.LENGTH_SHORT).show()
                    // Setelah pembayaran berhasil, Anda bisa mengosongkan keranjang jika perlu
                    // keranjangDB.clearCart() // Uncomment jika ada method untuk menghapus keranjang
                } else {
                    Toast.makeText(this, "Gagal menyimpan data pembayaran", Toast.LENGTH_SHORT).show()
                }
                finish() // Kembali ke activity sebelumnya
            } else {
                Toast.makeText(this, "Harap lengkapi semua field", Toast.LENGTH_SHORT).show()
            }
        }

        // Set listener untuk tombol batal
        batalButton.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
        }
    }

    private fun displayCartItems(cartItems: List<Product>) {
        itemPembayaranContainer.removeAllViews()
        totalPrice = 0

        for (item in cartItems) {
            val itemView = layoutInflater.inflate(R.layout.item_pembayaran, itemPembayaranContainer, false)

            val itemImage = itemView.findViewById<ImageView>(R.id.cartItemImage)
            val itemName = itemView.findViewById<TextView>(R.id.cartItemName)
            val itemPrice = itemView.findViewById<TextView>(R.id.cartItemPrice)
            val itemQuantity = itemView.findViewById<TextView>(R.id.cartItemQuantity)

            itemName.text = item.name
            itemPrice.text = "Rp. ${item.price}"
            itemQuantity.text = "1"

            totalPrice += item.price.toInt()

            itemPembayaranContainer.addView(itemView)
        }

        totalPriceTextView.text = "Total Harga: Rp. $totalPrice"
    }

    private fun getCurrentDate(): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        return sdf.format(java.util.Date())
    }
}
