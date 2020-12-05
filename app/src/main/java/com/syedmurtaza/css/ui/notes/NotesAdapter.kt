package com.syedmurtaza.css.ui.notes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syedmurtaza.css.databinding.NotesItemBinding
import com.syedmurtaza.css.models.Note

class NotesAdapter(
    private val clickListener: (note: Note) -> Unit,
    private val longClickListener: (note: Note) -> Boolean,
) :
    ListAdapter<Note, NotesAdapter.NoteViewHolder>(NotesCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(NotesItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false), clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.note = getItem(position)
        holder.bind()
    }

    class NoteViewHolder(
        private val binding: NotesItemBinding,
        private val onClickListener: (note: Note) -> Unit,
        private val longClickListener: (note: Note) -> Boolean,
    ) : RecyclerView.ViewHolder(binding.root) {

        var note: Note? = null

        fun bind() {
            val point = if(note?.points?.count() == 1) " point" else " points"
            binding.pointsCountTextView.text = note?.points?.count().toString() + point
            binding.topicTextView.text = note?.topic
            binding.root.setOnClickListener {
                onClickListener.invoke(note!!)
            }
            binding.root.setOnLongClickListener {
                longClickListener.invoke(note!!)
            }
        }
    }
}

class NotesCallBack : DiffUtil.ItemCallback<Note>() {
    override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
        return oldItem == newItem
    }
}