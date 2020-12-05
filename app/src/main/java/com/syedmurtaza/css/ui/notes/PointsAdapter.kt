package com.syedmurtaza.css.ui.notes

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.NoPointItemBinding
import com.syedmurtaza.css.databinding.PointItemBinding
import kotlinx.parcelize.Parcelize

class PointsAdapter(
    private val clickListener: (point: Point) -> Unit,
    private val longClickListener: (point: Point) -> Unit,
) :
    ListAdapter<Point, RecyclerView.ViewHolder>(PointCallBack()) {
    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).content.isEmpty()) return R.layout.no_point_item else R.layout.point_item
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.no_point_item) NoPointViewHolder(NoPointItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)) else PointViewHolder(PointItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false), clickListener, longClickListener)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PointViewHolder) {
            holder.point = getItem(position)
            holder.bind()
        }
    }

    class PointViewHolder(
        private val binding: PointItemBinding,
        private val clickListener: (point: Point) -> Unit,
        private val longClickListener: (point: Point) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        var point: Point? = null

        fun bind() {
            binding.pointItem.text = point?.content
            binding.countItem.text = point?.no.toString()
            binding.root.setOnClickListener {
                clickListener.invoke(point!!)
            }
            binding.root.setOnLongClickListener{
                longClickListener.invoke(point!!)
                return@setOnLongClickListener true
            }
        }
    }

    class NoPointViewHolder(binding: NoPointItemBinding) :
        RecyclerView.ViewHolder(binding.root)

}

class PointCallBack : DiffUtil.ItemCallback<Point>() {
    override fun areItemsTheSame(oldItem: Point, newItem: Point): Boolean {
        return oldItem.no == newItem.no
    }

    override fun areContentsTheSame(oldItem: Point, newItem: Point): Boolean {
        return oldItem == newItem
    }
}


@Parcelize
data class Point(val no: Int = 0, val content: String = "") : Parcelable