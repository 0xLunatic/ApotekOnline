package com.example.apotekonline.constructor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class PembayaranDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "apotekonline.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_NAME = "pembayaran"
        const val COLUMN_ID = "id"
        const val COLUMN_NAMA_PEMESAN = "nama_pemesan"
        const val COLUMN_ALAMAT_PENGIRIMAN = "alamat_pengiriman"
        const val COLUMN_METODE_PEMBAYARAN = "metode_pembayaran"
        const val COLUMN_NOMOR_TELEPON = "nomor_telepon"
        const val COLUMN_TANGGAL = "tanggal"
        const val COLUMN_TOTAL_HARGA = "total_harga"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAMA_PEMESAN TEXT NOT NULL,
                $COLUMN_ALAMAT_PENGIRIMAN TEXT NOT NULL,
                $COLUMN_METODE_PEMBAYARAN TEXT NOT NULL,
                $COLUMN_NOMOR_TELEPON TEXT NOT NULL,
                $COLUMN_TANGGAL TEXT NOT NULL,
                $COLUMN_TOTAL_HARGA INTEGER NOT NULL
            )
        """.trimIndent()
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertPembayaran(
        namaPemesan: String,
        alamatPengiriman: String,
        metodePembayaran: String,
        nomorTelepon: String,
        tanggal: String,
        totalHarga: Int
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAMA_PEMESAN, namaPemesan)
            put(COLUMN_ALAMAT_PENGIRIMAN, alamatPengiriman)
            put(COLUMN_METODE_PEMBAYARAN, metodePembayaran)
            put(COLUMN_NOMOR_TELEPON, nomorTelepon)
            put(COLUMN_TANGGAL, tanggal)
            put(COLUMN_TOTAL_HARGA, totalHarga)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }

    fun getAllPembayaran(): List<Pembayaran> {
        val listPembayaran = mutableListOf<Pembayaran>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val namaPemesan = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_PEMESAN))
                val alamatPengiriman = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ALAMAT_PENGIRIMAN))
                val metodePembayaran = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_METODE_PEMBAYARAN))
                val nomorTelepon = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMOR_TELEPON))
                val tanggal = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL))
                val totalHarga = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_HARGA))

                listPembayaran.add(
                    Pembayaran(
                        id,
                        namaPemesan,
                        alamatPengiriman,
                        metodePembayaran,
                        nomorTelepon,
                        tanggal,
                        totalHarga
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return listPembayaran
    }
}
