package com.example.apotekonline

import KeranjangDB
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MenuPesanActivity : AppCompatActivity() {
    private val productNames = arrayOf("Obat 1", "Obat 2", "Obat 3", "Obat 4", "Obat 5", "Obat 6", "Obat 7", "Obat 8")
    private val productPrices = arrayOf("15000", "20000", "25000", "30000", "35000", "40000", "5000", "12000")
    private val productImages = arrayOf(R.drawable.ibuprofen, R.drawable.bg, R.drawable.bg, R.drawable.bg, R.drawable.bg, R.drawable.bg, R.drawable.bg, R.drawable.bg)
    private val productDescriptions = arrayOf("Deskripsi Obat 1", "Deskripsi Obat 2", "Deskripsi Obat 3", "Deskripsi Obat 4", "Deskripsi Obat 5", "Deskripsi Obat 6", "Deskripsi Obat 7","Deskripsi Obat 8")
    private lateinit var keranjangDB: KeranjangDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pemesanan_obat)

        // Inisialisasi database
        keranjangDB = KeranjangDB(this)

        val menuPesan: GridLayout = findViewById(R.id.menu_pesan)
        val keranjangButton: ImageButton = findViewById(R.id.keranjang)
        val backButton : ImageButton = findViewById(R.id.backButton)

        // Loop melalui nama dan harga produk, membuat CardView untuk masing-masing item
        for (i in productNames.indices) {
            // Inflate layout product_card ke dalam View
            val cardView = LayoutInflater.from(this).inflate(R.layout.product_card, menuPesan, false) as CardView

            // Set nama, harga, dan gambar produk
            val productNameTextView: TextView = cardView.findViewById(R.id.productName)
            val productPriceTextView: TextView = cardView.findViewById(R.id.productPrice)
            val productImageView: ImageView = cardView.findViewById(R.id.productImage) // Tambahkan ImageView
            val buyButton: Button = cardView.findViewById(R.id.beli)

            productNameTextView.text = productNames[i]
            productPriceTextView.text = "Rp. ${productPrices[i]}" // Kembalikan 'Rp.' di sini
            productImageView.setImageResource(productImages[i]) // Set gambar produk

            // Tambahkan CardView ke GridLayout
            menuPesan.addView(cardView)

            // Handle klik pada CardView untuk menuju DetailActivity
            // Saat mengklik CardView untuk detail produk
            cardView.setOnClickListener {
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("EXTRA_NAME", productNames[i])
                    putExtra("EXTRA_PRICE", productPrices[i])
                    putExtra("EXTRA_IMAGE", productImages[i])
                    putExtra("EXTRA_DESCRIPTION", productDescriptions[i])
                }
                startActivity(intent)
            }


            // Handle button click untuk membeli produk
            buyButton.setOnClickListener {
                val productName = productNames[i]
                val productPrice = productPrices[i] // Mengonversi harga ke Int
                val productImage = productImages[i] // Ini adalah Int
                val productDescriptions = productDescriptions[i]
                // Tambahkan produk ke dalam keranjang (ke database)
                keranjangDB.addItemToCart(Product(productName, productPrice, productImage, productDescriptions))

                // Menampilkan pesan bahwa produk berhasil ditambahkan
                Toast.makeText(this, "$productName berhasil ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
            }
        }

        // Menangani klik pada tombol keranjang untuk melihat isi keranjang
        keranjangButton.setOnClickListener {
            // Arahkan ke KeranjangActivity
            startActivity(Intent(this, KeranjangActivity::class.java))
        }

        backButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }
}
