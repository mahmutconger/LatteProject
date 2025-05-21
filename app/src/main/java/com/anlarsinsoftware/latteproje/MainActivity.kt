package com.anlarsinsoftware.latteproje

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

    }
    fun taramaPage(view:View){
        startActivity(Intent(this,TaramaActivity::class.java))
    }
    fun urunlerPage(view:View){
        startActivity(Intent(this,UrunlerActivity::class.java))
    }
    fun hataliUrunPage(view:View){
        startActivity(Intent(this,HataliUrunActivity::class.java))
    }
    fun raporPage(view:View){
       // startActivity(Intent(this,TaramaActivity::class.java))
        Toast.makeText(this,"RaporSayfasi",Toast.LENGTH_SHORT).show()
    }

}