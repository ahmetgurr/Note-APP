package com.example.mynoteapplast

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference

class NoteAdapter (private val mContext: Context,
                   private val notesList: List<Notes>,
                   private val refNotes: DatabaseReference
)
    : RecyclerView.Adapter<NoteAdapter.CardTasarimTutucu>(){


    inner class CardTasarimTutucu(tasarim: View) : RecyclerView.ViewHolder(tasarim){
        var textNoteBaslik: TextView
        var textNoteIcerik: TextView
        var imageViewNokta: ImageView

        init {
            textNoteBaslik = tasarim.findViewById(R.id.textNoteBaslik)
            textNoteIcerik = tasarim.findViewById(R.id.textNoteIcerik)
            imageViewNokta = tasarim.findViewById(R.id.imageViewNokta)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardTasarimTutucu {
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.notes_card_design, parent, false)
        return CardTasarimTutucu(tasarim)
    }


    override fun getItemCount(): Int {
        return notesList.size
    }


    override fun onBindViewHolder(holder: CardTasarimTutucu, position: Int) {
        val not = notesList.get(position)

        holder.textNoteBaslik.text = "${not.note_baslik}"
        holder.textNoteIcerik.text = "${not.note_icerik}"

        holder.imageViewNokta.setOnClickListener {
            val popupMenu = PopupMenu(mContext, holder.imageViewNokta)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_sil -> {
                        Snackbar.make(
                            holder.imageViewNokta,
                            "${not.note_baslik}-${not.note_icerik} silinsin mi? ",
                            Snackbar.LENGTH_SHORT
                        )
                            .setAction("EVET") {
                                refNotes.child(not.note_id!!).removeValue()

                            }.show()
                        true
                    }

                    R.id.action_guncelle -> {
                        alertGoster(not)

                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }





    fun alertGoster(kisi: Notes){
        val tasarim = LayoutInflater.from(mContext).inflate(R.layout.alert_design,null)
        val editTextAd = tasarim.findViewById(R.id.editTextBaslik) as EditText
        val editTextTel = tasarim.findViewById(R.id.editTextIcerik) as EditText

        editTextAd.setText(kisi.note_baslik)
        editTextTel.setText(kisi.note_icerik)

        val ad = AlertDialog.Builder(mContext)
        ad.setTitle("Notu Güncelle")
        ad.setView(tasarim)
        ad.setPositiveButton("Güncelle"){dialogInterface,i->
            val notBaslik =editTextAd.text.toString().trim()
            val notIcerik =editTextTel.text.toString().trim()

            val bilgiler = HashMap<String,Any>()
            bilgiler.put("note_baslik",notBaslik)
            bilgiler.put("note_icerik",notIcerik)

            refNotes.child(kisi.note_id!!).updateChildren(bilgiler)//update işlevini burada kullanarak güncelleme yapıyoruz

            Toast.makeText(mContext,"$notBaslik-$notIcerik", Toast.LENGTH_SHORT).show()
        }
        ad.setNegativeButton("İptal"){dialogInterface,i->
        }
        ad.create().show()
    }


}