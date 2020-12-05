package com.syedmurtaza.css.ui.vocabulary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.SpacerItemBinding
import com.syedmurtaza.css.databinding.VocabularyItemBinding
import com.syedmurtaza.css.models.Vocabulary

class VocabularyAdapter(
    private val longClickListener: (vocabulary: Vocabulary) -> Boolean,
) :
    ListAdapter<Vocabulary, RecyclerView.ViewHolder>(VocabularyCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.vocabulary_item) VocabularyViewHolder(VocabularyItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false), longClickListener) else
            SpacerViewHolder(SpacerItemBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).id.isEmpty()) R.layout.spacer_item else R.layout.vocabulary_item
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is VocabularyViewHolder){
            holder.vocabulary = getItem(position)
            holder.bind()
        }
    }

    class VocabularyViewHolder(
        private val binding: VocabularyItemBinding,
        private val longClickListener: (vocabulary: Vocabulary) -> Boolean,
    ) : RecyclerView.ViewHolder(binding.root) {

        var vocabulary: Vocabulary? = null

        fun bind() {
            binding.wordTextView.text = vocabulary?.word
            binding.meaningTextView.text = vocabulary?.meaning
            binding.synonymTextView.text = vocabulary?.synonym
            binding.exampleTextView.text = vocabulary?.example
            binding.root.setOnLongClickListener {
                longClickListener.invoke(vocabulary!!)
            }
        }
    }

    class SpacerViewHolder(binding: SpacerItemBinding) : RecyclerView.ViewHolder(binding.root)

}

class VocabularyCallback : DiffUtil.ItemCallback<Vocabulary>() {
    override fun areItemsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Vocabulary, newItem: Vocabulary): Boolean {
        return oldItem == newItem
    }
}