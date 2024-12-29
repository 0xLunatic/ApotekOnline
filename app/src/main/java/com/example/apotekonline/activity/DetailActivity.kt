package com.example.apotekonline.activity

import com.example.apotekonline.constructor.KeranjangDB
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.constructor.Product
import com.example.apotekonline.R
import com.example.apotekonline.databinding.DetailObatBinding

class DetailActivity : AppCompatActivity() {
    private lateinit var cartIcon: ImageButton
    private lateinit var binding: DetailObatBinding
    private lateinit var keranjangDB: KeranjangDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inisialisasi binding
        binding = DetailObatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi database
        keranjangDB = KeranjangDB(this)

        // Mengambil data dari Intent
        val productName = intent.getStringExtra("EXTRA_NAME") ?: "N/A"
        val productPrice = intent.getStringExtra("EXTRA_PRICE") ?: "N/A"
        val productImageResId = intent.getIntExtra("EXTRA_IMAGE", 0)
        val productDescription = intent.getStringExtra("EXTRA_DESCRIPTION") ?: "Tidak ada deskripsi"

        // Set data ke tampilan
        binding.productImageView.setImageResource(productImageResId)
        binding.productNameTextView.text = productName  // Mengatur nama produk
        binding.productPriceTextView.text = "Rp. $productPrice"  // Mengatur harga produk
        binding.productDescriptionTextView.text = productDescription  // Mengatur deskripsi produk

        val product = Product(productName, productPrice, productImageResId, productDescription)

        val backButton : ImageButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            // Arahkan ke KeranjangActivity
            startActivity(Intent(this, MenuPesanActivity::class.java))
        }

        binding.buynow.setOnClickListener {
            addToCart(product)
        }

        // Inisialisasi cartIcon dan set listener
        cartIcon = findViewById(R.id.cart)
        cartIcon.setOnClickListener {
            // Mulai KeranjangActivity
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addToCart(product: Product) {
        val result = keranjangDB.addItemToCart(product)
        if (result != -1L) {  // Periksa apakah penambahan berhasil (mengembalikan ID baris)
            Toast.makeText(this, "${product.name} telah ditambahkan ke keranjang", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Gagal menambahkan ke keranjang", Toast.LENGTH_SHORT).show()
        }
    }
}
