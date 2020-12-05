package com.syedmurtaza.css.ui.notes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.NotesDataDialogBinding
import com.syedmurtaza.css.models.Note

open class NoteDialogFragment(private val clickListener: (note: Note) -> Unit) : DialogFragment() {
    private lateinit var binding: NotesDataDialogBinding
    private var editedPoint: Point? = null

    val points = mutableListOf<Point>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = NotesDataDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        points.add(Point(0, ""))

        val bundle = requireArguments()
        val subjectId = bundle.getString(KEY_STRING_SUBJECT_ID)
        val note = bundle.getParcelable<Note>(KEY_NOTE_CURRENT)

        if (note != null) {
            binding.noteAddBtn.text = "UPDATE"
            binding.topicName.setText(note.topic)
            points.clear()
            var count = 1
            for (item in note.points)
                points.add(item.copy(no = count++))
        }

        val pointAdapter = PointsAdapter({
            binding.pointText.setText(it.content)
            binding.textInputLayout2.setStartIconDrawable(R.drawable.ic_baseline_close_24)
            editedPoint = it
        }, {})
        pointAdapter.submitList(points.toList())

        binding.recyclerView.apply {
            adapter = pointAdapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        binding.textInputLayout2.setEndIconOnClickListener {
            if (editedPoint == null) {
                points.add(Point(1, binding.pointText.text.toString()))
                points.remove(Point(0, ""))
                binding.textInputLayout2.startIconDrawable = null
            } else {
                points.removeAt(editedPoint?.no!! - 1)
                points.add(editedPoint?.no!! - 1,
                    Point(editedPoint?.no!!, binding.pointText.text.toString()))
            }
            val tempList = listOfPoints()
            pointAdapter.submitList(tempList)
            binding.pointText.text?.clear()
            editedPoint = null
            checkButtonEnabling()
        }

        binding.noteAddBtn.setOnClickListener {
            val id = note?.id ?: ""
            val note = Note(id, subjectId!!, binding.topicName.text.toString(), points.toList())
            clickListener.invoke(note)
            dismiss()
        }

        binding.noteCancelBtn.setOnClickListener {
            dismiss()
        }

        binding.topicName.doOnTextChanged { _, _, _, _ ->
            checkButtonEnabling()
        }

        binding.textInputLayout2.setStartIconOnClickListener {
            binding.pointText.text?.clear()
            editedPoint = null
            binding.textInputLayout2.startIconDrawable = null
        }

        ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            override fun onSwiped(
                viewHolder: RecyclerView.ViewHolder,
                direction: Int,
            ) {
                if (viewHolder is PointsAdapter.PointViewHolder) {
                    points.remove(viewHolder.point)
                    pointAdapter.submitList(listOfPoints())
                } else {
                    pointAdapter.notifyDataSetChanged()
                }
                checkButtonEnabling()
            }

        }).attachToRecyclerView(binding.recyclerView)
    }

    private fun listOfPoints(): List<Point> {
        var count = 1
        return points.map {
            return@map Point(count++, it.content)
        }.toList()
    }

    override fun getTheme(): Int {
        return R.style.DialogTheme
    }

    private fun checkButtonEnabling() {
        binding.noteAddBtn.isEnabled = binding.topicName.text.toString().isNotEmpty() &&
                points[0].content.isNotEmpty()
    }

    companion object {
        const val KEY_STRING_SUBJECT_ID = "subjectId"
        const val KEY_NOTE_CURRENT = "note"
    }
}