package com.example.apotekonline

import KeranjangDB
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class PembayaranActivity : AppCompatActivity() {
    private lateinit var pembayaranDB: PembayaranDB
    private lateinit var keranjangDB: KeranjangDB
    private lateinit var konfirmasiButton: Button
    private lateinit var batalButton: Button
    private lateinit var totalPriceTextView: TextView
    private lateinit var itemPembayaranContainer: LinearLayout
    private lateinit var layananSpinner: Spinner
    private lateinit var pembayaranSpinner: Spinner

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
        layananSpinner = findViewById(R.id.pelayananSpinner)
        pembayaranSpinner = findViewById(R.id.pembayaranSpinner)

        // Data Spinner
        val layananOptions = listOf("Diantar ke Rumah", "Ambil di Apotek")
        val pembayaranDiantar = listOf("Transfer Bank", "GoPay", "LinkAja", "ShopeePay", "OVO")
        val pembayaranAmbil = listOf("Transfer Bank", "GoPay", "LinkAja", "ShopeePay", "OVO", "Cash")

        // Adapter untuk Spinner Layanan
        val layananAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, layananOptions)
        layananAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        layananSpinner.adapter = layananAdapter

        // Listener untuk Spinner Layanan
        layananSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLayanan = layananOptions[position]

                // Update Spinner Metode Pembayaran
                val pembayaranOptions = if (selectedLayanan == "Diantar ke Rumah") {
                    pembayaranDiantar
                } else {
                    pembayaranAmbil
                }
                val pembayaranAdapter = ArrayAdapter(this@PembayaranActivity, android.R.layout.simple_spinner_item, pembayaranOptions)
                pembayaranAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                pembayaranSpinner.adapter = pembayaranAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Mengambil semua item dari keranjang
        val cartItems = keranjangDB.getAllCartItems()

        if (cartItems.isEmpty()) {
            Toast.makeText(this, "Keranjang kosong. Silakan tambahkan item.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Menampilkan semua item di UI
        displayCartItems(cartItems)

        // Mengambil tanggal saat ini
        val currentDate = getCurrentDate()
        findViewById<TextView>(R.id.tanggalText).text = currentDate

        findViewById<EditText>(R.id.alamatPengirimanEditText).setOnClickListener {
            val dialog = MapsStaticDialogFragment()
            dialog.setOnAddressSelectedListener { address ->
                findViewById<EditText>(R.id.alamatPengirimanEditText).setText(address)
            }
            dialog.show(supportFragmentManager, "MapsStaticDialogFragment")
        }


        // Listener tombol konfirmasi pembayaran
        konfirmasiButton.setOnClickListener {
            handlePayment(cartItems, currentDate)
        }

        // Listener tombol batal pembayaran
        batalButton.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    /**
     * Menampilkan item yang ada di keranjang.
     */
    private fun displayCartItems(cartItems: List<Product>) {
        itemPembayaranContainer.removeAllViews()
        totalPrice = 0

        for (item in cartItems) {
            val itemView = layoutInflater.inflate(R.layout.item_pembayaran, itemPembayaranContainer, false)

            val itemName = itemView.findViewById<TextView>(R.id.cartItemName)
            val itemPrice = itemView.findViewById<TextView>(R.id.cartItemPrice)
            val itemQuantity = itemView.findViewById<TextView>(R.id.cartItemQuantity)

            // Set item name, price, and quantity
            itemName.text = item.name
            itemPrice.text = "Rp. ${item.price}"
            itemQuantity.text = "1"

            // Hitung total harga
            totalPrice += item.price.toInt()

            itemPembayaranContainer.addView(itemView)
        }

        totalPriceTextView.text = "Total Harga: Rp. $totalPrice"
    }

    /**
     * Proses pembayaran.
     */
    private fun handlePayment(cartItems: List<Product>, currentDate: String) {
        val namaPemesan = findViewById<EditText>(R.id.namaPemesanEditText).text.toString().trim()
        val alamatPengiriman = findViewById<EditText>(R.id.alamatPengirimanEditText).text.toString().trim()
        val nomorTelepon = findViewById<EditText>(R.id.nomorTeleponEditText).text.toString().trim()
        val selectedLayanan = layananSpinner.selectedItem?.toString()
        val selectedPembayaran = pembayaranSpinner.selectedItem?.toString()

        if (namaPemesan.isEmpty() || alamatPengiriman.isEmpty() || nomorTelepon.isEmpty() || selectedLayanan == null || selectedPembayaran == null) {
            Toast.makeText(this, "Harap lengkapi semua field.", Toast.LENGTH_SHORT).show()
            return
        }

        val jumlahObat = cartItems.size // Hitung jumlah total item di keranjang

        val result = pembayaranDB.insertPembayaran(
            namaPemesan,
            alamatPengiriman,
            nomorTelepon,
            totalPrice,
            currentDate,
            jumlahObat
        )

        if (result != -1L) {
            Toast.makeText(this, "Pembayaran berhasil untuk $namaPemesan.", Toast.LENGTH_SHORT).show()

            // Uncomment jika ingin mengosongkan keranjang
            // keranjangDB.clearCart()

            finish() // Kembali ke activity sebelumnya
        } else {
            Toast.makeText(this, "Gagal menyimpan data pembayaran.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCurrentDate(): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.format(Date())
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal mendapatkan tanggal.", Toast.LENGTH_SHORT).show()
            ""
        }
    }
}
