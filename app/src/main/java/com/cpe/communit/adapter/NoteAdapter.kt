package com.cpe.communit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cpe.communit.R
import com.cpe.communit.entity.Note
import kotlinx.android.synthetic.main.notes_rv_item.view.*

class NoteAdapter : RecyclerView.Adapter<NoteViewHolder>() {
    private var notes = emptyList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.notes_rv_item, parent, false)
        return NoteViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val curPlace = notes[position]
        holder.title.text = curPlace.title
        holder.description.text = curPlace.description
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    internal fun setNotes(notes: List<Note>) {
        this.notes = notes
        notifyDataSetChanged()
    }
}

class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val title: TextView = itemView.note_item_title
    val description: TextView = itemView.note_item_description
    val moreToggle: ImageButton = itemView.note_item_more
}