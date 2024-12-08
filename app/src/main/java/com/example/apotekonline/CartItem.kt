package com.example.apotekonline

data class CartItem(
    val name: String,
    val price: Int,
    val imageResId: Int,
    var quantity: Int = 0
)
