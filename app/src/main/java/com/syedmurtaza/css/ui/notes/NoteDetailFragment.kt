package com.syedmurtaza.css.ui.notes

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.syedmurtaza.css.databinding.FragmentNoteDetailBinding
import com.syedmurtaza.css.models.Note

class NoteDetailFragment : Fragment() {
    private lateinit var binding: FragmentNoteDetailBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNoteDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.appBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        val bundle = requireArguments()
        val note = bundle.getParcelable<Note>(NoteDialogFragment.KEY_NOTE_CURRENT)

        binding.topicText.text = note?.topic
        val pointAdapter = PointsAdapter({}, {
            val data = ClipData.newPlainText("text", it.content)
            val clipboardManager =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(data)
            Toast.makeText(context, "Point save to clipboard.", Toast.LENGTH_SHORT).show()
        })

        var count = 1
        pointAdapter.submitList(note?.points?.map { point ->
            return@map point.copy(no = count++, content = point.content)
        })

        binding.pointsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pointAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }
}