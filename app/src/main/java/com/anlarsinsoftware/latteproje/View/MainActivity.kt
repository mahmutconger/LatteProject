package com.anlarsinsoftware.latteproje.View

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anlarsinsoftware.latteproje.R
import com.anlarsinsoftware.latteproje.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity() {

     private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.raporBottom.setOnClickListener {

            val bottomSheetDialog = BottomSheetDialog(this)
            val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_layout, null)
            bottomSheetDialog.setContentView(view)
            bottomSheetDialog.show()

            }
    }
    fun taramaPage(view:View){
        startActivity(Intent(this, TaramaActivity::class.java))
    }
    fun urunlerPage(view:View){
        startActivity(Intent(this, UrunlerActivity::class.java))
    }
    fun hataliUrunPage(view:View){
        startActivity(Intent(this, HataliUrunActivity::class.java))
    }
    fun raporPage(view:View){
       // startActivity(Intent(this,TaramaActivity::class.java))
        Toast.makeText(this,"RaporSayfasi",Toast.LENGTH_SHORT).show()
    }

}