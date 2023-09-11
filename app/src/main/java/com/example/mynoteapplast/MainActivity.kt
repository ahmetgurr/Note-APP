package com.example.mynoteapplast


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mynoteapplast.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

import android.util.Log
import android.view.Menu
import androidx.appcompat.widget.SearchView
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.database.ktx.FirebaseDatabaseKtxRegistrar




class MainActivity : AppCompatActivity(),SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var notesList: ArrayList<Notes>
    private lateinit var adapter: NoteAdapter
    private lateinit var refNotes: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Not Uygulaması"
        setSupportActionBar(binding.toolbar)//uygulamanın ust çubugunu  özelleştirir

        binding.rv.layoutManager = LinearLayoutManager(this)//liste verileri göruntulemek için

        //database oluşturma
        val db = FirebaseDatabase.getInstance()
        refNotes = db.getReference("notlar")

        notesList = ArrayList()

        adapter = NoteAdapter(this,notesList,refNotes)
        binding.rv.adapter = adapter
        allNotes()

        binding.fab.setOnClickListener {
            alertGoster()
        }
    }

    //toolbar atama ust cubuga
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)

        val item = menu?.findItem(R.id.action_ara)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }


    // class ksımına SearchView.OnQueryTextListener yadıktan sonra otoamtık ımpleemnt etmemızı ıstıyor
    override fun onQueryTextSubmit(query: String): Boolean {
        aramaYap(query)
        Log.e("gonderilen arama",query.toString())
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
        aramaYap(newText)
        Log.e("Harf girdikçe",newText.toString())
        return true
    }
    fun aramaYap(aramaKelime:String){
        refNotes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()

                for (c in snapshot.children){
                    val kisi = c.getValue(Notes::class.java)

                    if (kisi !=null){
                        if (kisi.note_baslik!!.contains(aramaKelime)){
                            kisi.note_icerik = c.key
                            notesList.add(kisi)
                        }
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }



    fun alertGoster(){
        val tasarim =LayoutInflater.from(this).inflate(R.layout.alert_design,null)
        val editTextBaslik = tasarim.findViewById(R.id.editTextBaslik) as EditText
        val editTextIcerik = tasarim.findViewById(R.id.editTextIcerik) as EditText

        val ad = AlertDialog.Builder(this)
        ad.setTitle("Not Ekle")
        ad.setView(tasarim)
        ad.setPositiveButton("Ekle"){dialogInterface,i->
            val notBaslik =editTextBaslik.text.toString().trim()
            val notIcerik =editTextIcerik.text.toString().trim()// trim baştaki ve sondaki boşlukları kaldırır

            val not = Notes("",notBaslik,notIcerik)

            refNotes.push().setValue(not)

            Toast.makeText(applicationContext,"$notBaslik-$notIcerik", Toast.LENGTH_SHORT).show()
        }
        ad.setNegativeButton("İptal"){dialogInterface,i->
        }
        ad.create().show()
    }


    fun allNotes(){
        refNotes.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot){
                notesList.clear()

                for(c in snapshot.children){
                    val not = c.getValue(Notes::class.java)

                    if(not != null){
                        not.note_id = c.key
                        notesList.add(not)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }



}








