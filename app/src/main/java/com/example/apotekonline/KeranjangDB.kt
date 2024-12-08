import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.apotekonline.Product

class KeranjangDB(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Keranjang.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "cart"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_PRICE = "price"
        private const val COLUMN_IMAGE_RES_ID = "imageResId"
        private const val COLUMN_DESCRIPTION = "description"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Membuat tabel keranjang
        val createTable = ("CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COLUMN_NAME TEXT," +
                "$COLUMN_PRICE TEXT," +
                "$COLUMN_IMAGE_RES_ID INTEGER," +
                "$COLUMN_DESCRIPTION TEXT)")
        db?.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // Menghapus tabel lama jika ada versi baru
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Fungsi untuk menghapus item dari keranjang berdasarkan nama produk
    fun removeItemFromCart(product: Product): Boolean {
        val db = this.writableDatabase
        val result = db.delete(TABLE_NAME, "$COLUMN_NAME=?", arrayOf(product.name))
        db.close()
        return result > 0 // Return true jika item berhasil dihapus, false jika tidak
    }

    // Fungsi untuk mendapatkan semua item dari keranjang
    fun getAllCartItems(): List<Product> {
        val cartItems = mutableListOf<Product>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                val price = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRICE))
                val imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IMAGE_RES_ID))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))

                val product = Product(name, price, imageResId, description)
                cartItems.add(product)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return cartItems
    }

    // Fungsi untuk menambahkan item ke keranjang
    fun addItemToCart(product: Product): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, product.name)
            put(COLUMN_PRICE, product.price)
            put(COLUMN_IMAGE_RES_ID, product.imageResId)
            put(COLUMN_DESCRIPTION, product.description)
        }
        val result = db.insert(TABLE_NAME, null, values)
        db.close()
        return result
    }
}
