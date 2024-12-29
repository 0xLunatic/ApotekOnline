package com.example.apotekonline.constructor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class PembayaranDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Pembayaran.db"
        private const val DATABASE_VERSION = 1

        // Tabel pembayaran
        private const val TABLE_PEMBAYARAN = "pembayaran"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAMA = "nama_pemesan"
        private const val COLUMN_ALAMAT = "alamat_pengiriman"
        private const val COLUMN_TELEFON = "nomor_telepon"
        private const val COLUMN_TOTAL_HARGA = "total_harga"
        private const val COLUMN_TANGGAL = "tanggal"
        private const val COLUMN_JUMLAH_OBAT = "jumlah_obat" // Kolom untuk jumlah obat

        // Tabel keranjang
        private const val TABLE_KERANJANG = "keranjang"
        private const val COLUMN_PRODUCT_NAME = "name"
        private const val COLUMN_PRODUCT_PRICE = "price"
        private const val COLUMN_PRODUCT_IMAGE_RES_ID = "imageResId"
        private const val COLUMN_PRODUCT_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Buat tabel pembayaran dengan kolom tanggal
        val createPembayaranTableQuery = ("CREATE TABLE $TABLE_PEMBAYARAN (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_NAMA TEXT NOT NULL, " +
                "$COLUMN_ALAMAT TEXT NOT NULL, " +
                "$COLUMN_TELEFON TEXT NOT NULL, " +
                "$COLUMN_TOTAL_HARGA INTEGER NOT NULL, " +
                "$COLUMN_TANGGAL TEXT NOT NULL, " +
                "$COLUMN_JUMLAH_OBAT INTEGER NOT NULL" + // Menambahkan kolom jumlah obat
                ")")

        db.execSQL(createPembayaranTableQuery)

        // Buat tabel keranjang
        val createKeranjangTableQuery = ("CREATE TABLE $TABLE_KERANJANG (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_PRODUCT_NAME TEXT NOT NULL, " +
                "$COLUMN_PRODUCT_PRICE TEXT NOT NULL, " +
                "$COLUMN_PRODUCT_IMAGE_RES_ID INTEGER NOT NULL, " +
                "$COLUMN_PRODUCT_DESCRIPTION TEXT NOT NULL" +
                ")")

        db.execSQL(createKeranjangTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PEMBAYARAN")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_KERANJANG")
        onCreate(db)
    }

    // Ubah insertPembayaran untuk menerima tanggal dan jumlah obat
    fun insertPembayaran(nama: String, alamat: String, nomorTelepon: String, totalHarga: Int, tanggal: String, jumlahObat: Int): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA, nama)
            put(COLUMN_ALAMAT, alamat)
            put(COLUMN_TELEFON, nomorTelepon)
            put(COLUMN_TOTAL_HARGA, totalHarga)
            put(COLUMN_TANGGAL, tanggal)
            put(COLUMN_JUMLAH_OBAT, jumlahObat) // Menambahkan jumlah obat
        }

        val result = db.insert(TABLE_PEMBAYARAN, null, values)
        db.close()
        return result // Mengembalikan ID dari baris yang ditambahkan
    }

    // Fungsi untuk mengambil semua data pembayaran
    fun getAllPembayaran(): List<Pembayaran> {
        val list = mutableListOf<Pembayaran>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_PEMBAYARAN", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID))
                val nama = cursor.getString(cursor.getColumnIndex(COLUMN_NAMA))
                val alamat = cursor.getString(cursor.getColumnIndex(COLUMN_ALAMAT))
                val nomorTelepon = cursor.getString(cursor.getColumnIndex(COLUMN_TELEFON))
                val totalHarga = cursor.getInt(cursor.getColumnIndex(COLUMN_TOTAL_HARGA))
                val tanggal = cursor.getString(cursor.getColumnIndex(COLUMN_TANGGAL)) // Mengambil tanggal
                val jumlahObat = cursor.getInt(cursor.getColumnIndex(COLUMN_JUMLAH_OBAT)) // Mengambil jumlah obat

                list.add(Pembayaran(id, nama, alamat, nomorTelepon, totalHarga, tanggal, jumlahObat))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }

    // Fungsi untuk mengambil semua item di keranjang
    fun getAllCartItems(): List<Product> {
        val cartItems = mutableListOf<Product>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_KERANJANG", null)
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME))
                val price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE)) // Harga sebagai string
                val imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(
                    COLUMN_PRODUCT_IMAGE_RES_ID
                ))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(
                    COLUMN_PRODUCT_DESCRIPTION
                ))

                val product = Product(name, price, imageResId, description)
                cartItems.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return cartItems
    }
}
