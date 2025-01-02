package com.example.apotekonline.constructor

data class Pembayaran(
    val id: Int = 0,
    val namaPemesan: String,
    val alamatPengiriman: String,
    val metodePembayaran: String,
    val nomorTelepon: String,
    val tanggal: String,
    val totalHarga: Int
)
