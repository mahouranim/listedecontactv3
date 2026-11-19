package com.example.listedecontactsversion2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.listedecontactsversion2.databinding.ItemContactBinding

class ContactAdapter(private val onClick: (Contact) -> Unit) :
    ListAdapter<Contact, ContactAdapter.VH>(Diff) {

    class VH(val b: ItemContactBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = getItem(position)
        holder.b.tvNom.text = c.nom
        holder.b.tvPrenom.text = c.prenom
        holder.b.tvTel.text = c.telephone
        holder.b.imgPhoto.showContactPhoto(c.photoUri)
        // On remonte l'objet (donc son id), jamais la position -> pas d'erreur d'indice après filtrage
        holder.b.root.setOnClickListener { onClick(c) }
    }

    private object Diff : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(a: Contact, b: Contact) = a.id == b.id
        override fun areContentsTheSame(a: Contact, b: Contact) = a == b
    }
}
