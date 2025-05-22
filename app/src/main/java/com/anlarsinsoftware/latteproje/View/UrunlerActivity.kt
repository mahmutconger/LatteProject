package com.anlarsinsoftware.latteproje.View

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.anlarsinsoftware.latteproje.Databases.UrunlerDatabase
import com.anlarsinsoftware.latteproje.UrunlerAdapter
import com.anlarsinsoftware.latteproje.databinding.ActivityUrunlerBinding

class UrunlerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUrunlerBinding
    private lateinit var adapter: UrunlerAdapter
    private lateinit var sirala : String
    private lateinit var veriListesi : MutableList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUrunlerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = UrunlerDatabase(this)
        sirala="ASC"
         veriListesi = db.listele(sirala).mapIndexed{index,veri->"${index+1}.    $veri"}
            .toMutableList()





        veriListesi = db.listele(sirala).mapIndexed{index,veri->"${index+1}.    $veri"}
            .toMutableList()

        binding.tersSiralaBtn.visibility=View.GONE
        binding.siralaBtn.setOnClickListener {
            sirala = "DESC"
            veriListesi.clear()
            veriListesi.addAll(db.listele(sirala).mapIndexed { index, veri -> "${index + 1}    $veri" })
            adapter.notifyDataSetChanged()

            binding.tersSiralaBtn.visibility = View.VISIBLE
            binding.siralaBtn.visibility = View.GONE
        }

        binding.tersSiralaBtn.setOnClickListener {
            sirala = "ASC"
            veriListesi.clear()
            veriListesi.addAll(db.listele(sirala).mapIndexed { index, veri -> "${index + 1}    $veri" })
            adapter.notifyDataSetChanged()

            binding.siralaBtn.visibility = View.VISIBLE
            binding.tersSiralaBtn.visibility = View.GONE
        }

        adapter = UrunlerAdapter(veriListesi)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter


        binding.deleteBtn.setOnClickListener {
            db.temizle()
            adapter.veriler.clear()
            adapter.notifyDataSetChanged()
        }

        val sayi = db.urunSayisiniGetir()
        binding.textView.text= "Toplam Sağlam Ürün : $sayi"
    }

    fun backImageClick(view: View){
        startActivity(Intent(this, MainActivity::class.java))
    }
}