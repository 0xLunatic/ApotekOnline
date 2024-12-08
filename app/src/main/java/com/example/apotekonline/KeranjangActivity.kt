package com.example.apotekonline

import KeranjangDB
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.apotekonline.R

class KeranjangActivity : AppCompatActivity() {
    private lateinit var keranjangDB: KeranjangDB

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keranjang)
        val addButton: Button = findViewById(R.id.addButton)
        val bayarButton: Button = findViewById(R.id.bayarButton)

        bayarButton.setOnClickListener {
            val intent = Intent(this, PembayaranActivity::class.java)
            startActivity(intent)
        }

        addButton.setOnClickListener {
            val intent = Intent(this, MenuPesanActivity::class.java)
            startActivity(intent)
        }

        // Initialize database
        keranjangDB = KeranjangDB(this)

        // Get all cart items
        val cartItems = keranjangDB.getAllCartItems()

        // Reference to the container where items will be added
        val cartItemsContainer = findViewById<LinearLayout>(R.id.cartItemsContainer)

        // Clear the container to prevent duplication
        cartItemsContainer.removeAllViews()

        // Display each item in the cart
        for (item in cartItems) {
            // Inflate the cart_item layout instead of the full keranjang layout
            val cartItemView = layoutInflater.inflate(R.layout.cart_item, cartItemsContainer, false)

            // Set item details
            val itemImage = cartItemView.findViewById<ImageView>(R.id.cartItemImage)
            val itemName = cartItemView.findViewById<TextView>(R.id.cartItemName)
            val itemPrice = cartItemView.findViewById<TextView>(R.id.cartItemPrice)
            val itemQuantity = cartItemView.findViewById<TextView>(R.id.banyakItem)

            itemImage.setImageResource(item.imageResId)
            itemName.text = item.name
            itemPrice.text = "Rp. ${item.price}"
            itemQuantity.text = "1"

            // Handle button clicks
            val removeButton = cartItemView.findViewById<ImageButton>(R.id.removeItemButton)
            val increaseButton = cartItemView.findViewById<ImageButton>(R.id.increaseButton)
            val decreaseButton = cartItemView.findViewById<ImageButton>(R.id.decreaseButton)

            removeButton.setOnClickListener {
                keranjangDB.removeItemFromCart(item)
                cartItemsContainer.removeView(cartItemView)
                updateTotalPrice()
            }

            increaseButton.setOnClickListener {
                val currentQuantity = itemQuantity.text.toString().toInt()
                itemQuantity.text = (currentQuantity + 1).toString()
                updateTotalPrice()
            }

            decreaseButton.setOnClickListener {
                val currentQuantity = itemQuantity.text.toString().toInt()
                if (currentQuantity > 1) {
                    itemQuantity.text = (currentQuantity - 1).toString()
                    updateTotalPrice()
                }
            }
            // Add the inflated view to the container
            cartItemsContainer.addView(cartItemView)
        }

        // Initialize total price
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        var total = 0
        val cartItemsContainer = findViewById<LinearLayout>(R.id.cartItemsContainer)

        for (i in 0 until cartItemsContainer.childCount) {
            val itemView = cartItemsContainer.getChildAt(i)
            val priceText = itemView.findViewById<TextView>(R.id.cartItemPrice).text.toString()
            val quantity = itemView.findViewById<TextView>(R.id.banyakItem).text.toString().toInt()

            val price = priceText.replace("Rp. ", "").toInt()
            total += price * quantity
        }

        findViewById<TextView>(R.id.totalPrice).text = "Rp. $total"
    }
}
