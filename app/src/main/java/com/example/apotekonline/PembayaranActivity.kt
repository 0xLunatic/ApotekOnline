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

    // Konstanta untuk request code
    companion object {
        private const val REQUEST_ALAMAT_PENGIRIMAN = 1001
        private const val REQUEST_PILIH_APOTEK = 2001
    }

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

        // Listener tombol konfirmasi pembayaran
        konfirmasiButton.setOnClickListener {
            val selectedLayanan = layananSpinner.selectedItem?.toString()

            if (selectedLayanan == "Diantar ke Rumah") {
                val namaPemesan = findViewById<EditText>(R.id.namaPemesanEditText).text.toString().trim()
                val nomorTelepon = findViewById<EditText>(R.id.nomorTeleponEditText).text.toString().trim()

                startActivityForResult(intent, REQUEST_ALAMAT_PENGIRIMAN)
            } else if (selectedLayanan == "Ambil di Apotek") {
                // Arahkan ke Pemilihan Apotek
                val intent = Intent(this, PilihApotekActivity::class.java)
                startActivityForResult(intent, REQUEST_PILIH_APOTEK) // Request code bisa disesuaikan
            }
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

    /**
     * Menangani data kembali dari AlamatPengirimanActivity atau PilihApotekActivity.
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ALAMAT_PENGIRIMAN && resultCode == RESULT_OK) {
            val namaPemesan = data?.getStringExtra("NAMA_PEMESAN")
            val nomorTelepon = data?.getStringExtra("NOMOR_TELEPON")
            val alamatPengiriman = data?.getStringExtra("ALAMAT_PENGIRIMAN")

            if (alamatPengiriman != null) {
                handlePaymentWithAddress(namaPemesan, alamatPengiriman, nomorTelepon)
            }
        }

        // Menangani hasil dari PilihApotekActivity
        if (requestCode == REQUEST_PILIH_APOTEK && resultCode == RESULT_OK) {
            val selectedApotek = data?.getStringExtra("SELECTED_APOTEK")
            if (selectedApotek != null) {
                Toast.makeText(this, "Anda memilih apotek: $selectedApotek", Toast.LENGTH_SHORT).show()
                // Lanjutkan proses pembayaran atau simpan apotek terpilih
            }
        }
    }

    private fun handlePaymentWithAddress(namaPemesan: String?, alamatPengiriman: String?, nomorTelepon: String?) {
        if (namaPemesan.isNullOrEmpty() || alamatPengiriman.isNullOrEmpty() || nomorTelepon.isNullOrEmpty()) {
            Toast.makeText(this, "Harap lengkapi semua field.", Toast.LENGTH_SHORT).show()
            return
        }

        // Lanjutkan proses pembayaran setelah alamat dikonfirmasi
        val cartItems = keranjangDB.getAllCartItems()
        val currentDate = getCurrentDate()
        handlePayment(cartItems, currentDate)
    }
}
