package com.example.apotekonline

data class Pembayaran(
    val id: Int,
    val nama: String,
    val alamat: String,
    val nomorTelepon: String,
    val totalHarga: Int,
    val tanggal: String, // Tambahkan tanggal jika perlu
    val jumlah_obat: Int // Tambahkan jumlah obat
)
