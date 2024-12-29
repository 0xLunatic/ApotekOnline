package com.example.apotekonline.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R
import com.example.apotekonline.constructor.KeranjangDB
import com.example.apotekonline.constructor.Product
import java.util.*

class PembayaranActivity : AppCompatActivity() {
    private lateinit var keranjangDB: KeranjangDB
    private lateinit var konfirmasiButton: Button
    private lateinit var itemPembayaranContainer: LinearLayout
    private lateinit var tanggalPembayaran: EditText
    private lateinit var layananSpinner: Spinner
    private lateinit var metodePembayaranSpinner: Spinner
    private lateinit var totalPriceText: TextView
    private lateinit var batalButton: Button

    private lateinit var namaPemesanEditText: EditText
    private lateinit var nomorTeleponEditText: EditText

    // In onCreate()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pembayaran)

        keranjangDB = KeranjangDB(this)

        itemPembayaranContainer = findViewById(R.id.itemPembayaranContainer)
        konfirmasiButton = findViewById(R.id.konfirmasiButton)
        tanggalPembayaran = findViewById(R.id.tanggalPembayaran)
        layananSpinner = findViewById(R.id.pelayananSpinner)
        metodePembayaranSpinner = findViewById(R.id.pembayaranSpinner)
        totalPriceText = findViewById(R.id.totalPrice)
        batalButton = findViewById(R.id.batalButton)

        namaPemesanEditText = findViewById(R.id.namaPemesanEditText)
        nomorTeleponEditText = findViewById(R.id.nomorTeleponEditText)

        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()

        val totalPriceInt = sharedPreferences.getInt("totalPrice", 0)
        val formattedPrice = totalPriceInt
        totalPriceText.text = "Total: Rp. $formattedPrice"

        val layananOptions = listOf("Diantar ke Rumah", "Ambil di Apotek")

        // Simplified payment methods list
        val paymentMethods = listOf("Transfer Bank", "GoPay", "LinkAja", "ShopeePay", "OVO", "Cash")

        val layananAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, layananOptions) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text = view.findViewById<TextView>(android.R.id.text1)
                text.setTextColor(Color.BLACK) // Set the selected item text color to black
                return view
            }
        }

        layananAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        layananSpinner.adapter = layananAdapter

// Set the payment methods adapter
        val pembayaranAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, paymentMethods) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val text = view.findViewById<TextView>(android.R.id.text1)
                text.setTextColor(Color.BLACK) // Set the selected item text color to black
                return view
            }
        }

        pembayaranAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        metodePembayaranSpinner.adapter = pembayaranAdapter

// Optional: Set the selected item programmatically (if you want to select one of the items)
        metodePembayaranSpinner.setSelection(0)

        layananSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: View?, position: Int, id: Long) {
                // No need to update payment methods, as it stays the same
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        tanggalPembayaran.setOnClickListener {
            val calendar = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                val formattedDate = "${dayOfMonth}/${month + 1}/$year"
                tanggalPembayaran.setText(formattedDate)
            }
            DatePickerDialog(this, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        konfirmasiButton.setOnClickListener {
            val namaPemesan = namaPemesanEditText.text.toString()
            val nomorTelepon = nomorTeleponEditText.text.toString()

            editor.putString("namaPemesan", namaPemesan)
            editor.putString("nomorTelepon", nomorTelepon)

            editor.putString("metodePembayaran", metodePembayaranSpinner.selectedItem.toString())

            editor.putString("alamatPengiriman", layananSpinner.selectedItem.toString())
            editor.putString("currentDate", tanggalPembayaran.text.toString())
            editor.putInt("totalPrice", totalPriceText.text.toString().replace("Total: Rp. ", "").toInt())

            editor.apply()

            Log.d("PembayaranActivity", "Nama Pemesan: $namaPemesan")
            Log.d("PembayaranActivity", "Nomor Telepon: $nomorTelepon")

            val intent = Intent(this, KonfirmasiPembayaranActivity::class.java)
            startActivity(intent)
        }

        batalButton.setOnClickListener {
            val intent = Intent(this, KeranjangActivity::class.java)
            startActivity(intent)
            finish()
        }

        populateItemPembayaranContainer()
    }


    private fun populateItemPembayaranContainer() {
        val cartItems = keranjangDB.getAllCartItems()

        itemPembayaranContainer.removeAllViews()

        var totalPrice = 0

        cartItems.forEach { product ->
            val itemView = layoutInflater.inflate(R.layout.cart_item, itemPembayaranContainer, false)

            val cartItemImage: ImageView = itemView.findViewById(R.id.cartItemImage)
            val cartItemName: TextView = itemView.findViewById(R.id.cartItemName)
            val cartItemPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
            val decreaseButton: ImageButton = itemView.findViewById(R.id.decreaseButton)
            val increaseButton: ImageButton = itemView.findViewById(R.id.increaseButton)
            val removeItemButton: ImageButton = itemView.findViewById(R.id.removeItemButton)
            val banyakItem: TextView = itemView.findViewById(R.id.banyakItem)

            cartItemName.text = product.name
            cartItemPrice.text = "Rp. ${product.price}"
            cartItemImage.setImageResource(product.imageResId)

            val itemPrice = product.price.toInt()
            val quantity = banyakItem.text.toString().toInt()
            totalPrice += itemPrice * quantity

            decreaseButton.setOnClickListener {
                val currentQuantity = banyakItem.text.toString().toInt()
                if (currentQuantity > 1) {
                    banyakItem.text = (currentQuantity - 1).toString()
                    totalPrice -= itemPrice
                    updateTotalPrice(totalPrice)
                }
            }

            increaseButton.setOnClickListener {
                val currentQuantity = banyakItem.text.toString().toInt()
                banyakItem.text = (currentQuantity + 1).toString()
                totalPrice += itemPrice
                updateTotalPrice(totalPrice)
            }

            removeItemButton.setOnClickListener {
                keranjangDB.removeItemFromCart(product)

                val quantity = banyakItem.text.toString().toInt()
                val itemPrice = product.price.toInt()
                totalPrice -= itemPrice * quantity

                itemPembayaranContainer.removeView(itemView)

                updateTotalPrice(totalPrice)

                Toast.makeText(this, "${product.name} removed from cart", Toast.LENGTH_SHORT).show()
            }

            itemPembayaranContainer.addView(itemView)
        }

        updateTotalPrice(totalPrice)
    }

    private fun updateTotalPrice(totalPrice: Int) {
        totalPriceText.text = "Total: Rp. $totalPrice"
    }
}
