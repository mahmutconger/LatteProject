package com.anlarsinsoftware.latteproje.Databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UrunlerDatabase(context: Context) : SQLiteOpenHelper(context, "UrunlerDB", null, 2) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE urunler (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tarih TEXT)"
        )
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS veri_istatistikleri" +
                " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "urun_sayisi INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS urunler")
        db.execSQL("DROP TABLE IF EXISTS veri_istatistikleri")
        onCreate(db)
    }


    fun kaydet(tarih: String) {
        writableDatabase.execSQL("INSERT INTO urunler (tarih) VALUES (?)", arrayOf(tarih))
    }

    fun listele(sirala: String): List<String> {
        val db = this.readableDatabase
        val siralama = if (sirala.uppercase() == "DESC") "DESC" else "ASC"
        val liste = mutableListOf<String>()

        val cursor = db.rawQuery("SELECT * FROM urunler ORDER BY id $siralama", null)
        if (cursor.moveToFirst()) {
            do {
                val tarih = cursor.getString(cursor.getColumnIndexOrThrow("tarih"))
                liste.add(tarih)
            } while (cursor.moveToNext())
        }
        cursor.close()

        // Ürün sayısını kaydet
        val urunSayisi = liste.size
        val dbWrite = this.writableDatabase
        dbWrite.execSQL("DELETE FROM veri_istatistikleri")
        dbWrite.execSQL("INSERT INTO veri_istatistikleri (urun_sayisi) VALUES ($urunSayisi)")

        return liste
    }

    fun urunSayisiniGetir(): Int {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT urun_sayisi FROM veri_istatistikleri LIMIT 1", null)
        var sayi = 0
        if (cursor.moveToFirst()) {
            sayi = cursor.getInt(cursor.getColumnIndexOrThrow("urun_sayisi"))
        }
        cursor.close()
        return sayi
    }



    fun temizle() {
        writableDatabase.execSQL("DELETE FROM urunler")
    }
}
