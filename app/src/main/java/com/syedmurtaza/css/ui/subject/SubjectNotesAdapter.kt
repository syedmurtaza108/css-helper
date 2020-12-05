package com.syedmurtaza.css.ui.subject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.NotesSubjectItemBinding
import com.syedmurtaza.css.databinding.SpacerItemBinding
import com.syedmurtaza.css.databinding.SubjectsItemBinding
import com.syedmurtaza.css.models.Subject

class SubjectNotesAdapter(
    private val clickListener: (subject: Subject) -> Unit
) :
    ListAdapter<Subject, SubjectNotesAdapter.SubjectViewHolder>(SubjectsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        return SubjectViewHolder(NotesSubjectItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false),
            clickListener)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SubjectViewHolder(
        private val binding: NotesSubjectItemBinding,
        private val onClickListener: (subject: Subject) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subject: Subject) {
            binding.subjectNameNotes.text = subject.name
            binding.root.setOnClickListener {
                onClickListener.invoke(subject)
            }

        }
    }

}