package com.anlarsinsoftware.latteproje.Databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class HataliUrunDatabase(context: Context) : SQLiteOpenHelper(context, "HataliUrunDB", null, 2) {
    // Versiyon 2 olmalı ki yeni tablo oluşturulsun

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE IF NOT EXISTS hatali_urunler (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "tarih TEXT)"
        )

        db.execSQL(
            "CREATE TABLE IF NOT EXISTS hata_istatistikleri (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "hata_sayisi INTEGER)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS hatali_urunler")
        db.execSQL("DROP TABLE IF EXISTS hata_istatistikleri")
        onCreate(db)
    }

    fun kaydet(tarih: String) {
        writableDatabase.execSQL("INSERT INTO hatali_urunler (tarih) VALUES (?)", arrayOf(tarih))
    }

    fun listele(sirala: String): List<String> {
        val liste = mutableListOf<String>()
        val siralama = if (sirala.uppercase() == "DESC") "DESC" else "ASC"
        val cursor = readableDatabase.rawQuery(
            "SELECT * FROM hatali_urunler ORDER BY id $siralama", null
        )
        while (cursor.moveToNext()) {
            val tarih = cursor.getString(cursor.getColumnIndexOrThrow("tarih"))
            liste.add(tarih)
        }
        cursor.close()

        // Toplam hata sayısını veritabanına kaydet
        val toplamHatalar = liste.size
        val db = this.writableDatabase
        db.execSQL("DELETE FROM hata_istatistikleri")
        db.execSQL("INSERT INTO hata_istatistikleri (hata_sayisi) VALUES ($toplamHatalar)")

        return liste
    }

    fun hataSayisiniGetir(): Int {
        val cursor = readableDatabase.rawQuery("SELECT hata_sayisi FROM hata_istatistikleri LIMIT 1", null)
        var sayi = 0
        if (cursor.moveToFirst()) {
            sayi = cursor.getInt(cursor.getColumnIndexOrThrow("hata_sayisi"))
        }
        cursor.close()
        return sayi
    }

    fun temizle() {
        writableDatabase.execSQL("DELETE FROM hatali_urunler")
        writableDatabase.execSQL("DELETE FROM hata_istatistikleri")
    }
}
