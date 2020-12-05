package com.syedmurtaza.css.ui.subject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.SpacerItemBinding
import com.syedmurtaza.css.databinding.SubjectsItemBinding
import com.syedmurtaza.css.models.NoteResponse
import com.syedmurtaza.css.models.Subject
import kotlinx.coroutines.flow.StateFlow

class SubjectsAdapter(
    private val notes: StateFlow<List<NoteResponse>>,
    private val clickListener: (subject: Subject) -> Unit,
    private val longClickListener: (subject: Subject) -> Boolean,
) :
    ListAdapter<Subject, RecyclerView.ViewHolder>(SubjectsCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.subjects_item) SubjectViewHolder(SubjectsItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false),
            clickListener,
            longClickListener,
            notes) else SpacerViewHolder(SpacerItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).id.isEmpty()) R.layout.spacer_item else R.layout.subjects_item
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SubjectViewHolder) {
            holder.subject = getItem(position)
            holder.bind()
        }
    }

    class SubjectViewHolder(
        private val binding: SubjectsItemBinding,
        private val onClickListener: (subject: Subject) -> Unit,
        private val longClickListener: (subject: Subject) -> Boolean,
        private val notes: StateFlow<List<NoteResponse>>,
    ) : RecyclerView.ViewHolder(binding.root) {

        var subject: Subject? = null

        fun bind() {
            val countNotes = notes.value.filter { it.subjectId == subject?.id }.count()
            val captionNotes = if(countNotes == 1) " note" else " notes"
            var countPoints = 0
            notes.value.filter { it.subjectId == subject?.id }.map {
                countPoints += it.points.count()
            }
            val captionPoints = if(countPoints == 1) " point" else " points"
            binding.idText.text = subject?.id
            binding.titleText.text = subject?.name
            binding.countText.text = countNotes.toString() + captionNotes
            binding.countText2.text = countPoints.toString() + captionPoints
            binding.root.setOnClickListener {
                onClickListener.invoke(subject!!)
            }
            binding.root.setOnLongClickListener {
                longClickListener.invoke(subject!!)
            }
        }
    }

    class SpacerViewHolder(binding: SpacerItemBinding) : RecyclerView.ViewHolder(binding.root)

}

class SubjectsCallback : DiffUtil.ItemCallback<Subject>() {
    override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
        return oldItem == newItem
    }
}