package com.syedmurtaza.css.ui.notes

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.transition.MaterialContainerTransform
import com.syedmurtaza.css.R
import com.syedmurtaza.css.databinding.FragmentNoteDetailBinding
import com.syedmurtaza.css.models.Note
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
class NoteDetailFragment : Fragment() {
    private lateinit var binding: FragmentNoteDetailBinding
    private lateinit var note: Note
    private val viewModel: NoteDetailViewModel by viewModel()

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

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.fragment
            duration = 300
            scrimColor = Color.TRANSPARENT
        }

        val bundle = requireArguments()
        note = bundle.getParcelable(NoteDialogFragment.KEY_NOTE_CURRENT)!!

        viewModel.selectedNote(note.id)


        val pointAdapter = PointsAdapter({}, {
            val data = ClipData.newPlainText("text", it.content)
            val clipboardManager =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(data)
            Toast.makeText(context, "Point save to clipboard.", Toast.LENGTH_SHORT).show()
        })

        binding.pointsList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = pointAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }

        binding.deleteBtn.setOnClickListener {
            deleteConfirmationDialog().show()
        }

        binding.editBtn.setOnClickListener {
            val dialog = NoteDialogFragment { note ->
                viewModel.updateNote(note = note) { isAdded ->
                    if (isAdded)
                        Toast.makeText(context,
                            "Note has been updated successfully.",
                            Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(context,
                            "Note has NOT been updated. Try Again!",
                            Toast.LENGTH_SHORT).show()
                }
            }
            dialog.arguments = bundleOf(
                NoteDialogFragment.KEY_NOTE_CURRENT to note,
                NoteDialogFragment.KEY_STRING_SUBJECT_ID to note.subjectId)
            dialog.show(parentFragmentManager, "")
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.note.collect {
                note = it
                binding.topicText.text = note.topic
                var count = 1
                pointAdapter.submitList(note.points.map { point ->
                    return@map point.copy(no = count++, content = point.content)
                })
            }
        }

        setFragmentResult("", bundleOf())
    }

    private fun deleteConfirmationDialog(): AlertDialog {
        return activity.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle("Note would be deleted permanently.")
                setMessage("Are you sure to delete the selected note?")
                setPositiveButton("Yes",
                    DialogInterface.OnClickListener { _, _ ->
                        viewModel.deleteNote(note.id) { response ->
                            if (response) {
                                Toast.makeText(context,
                                    "Note has been deleted successfully.",
                                    Toast.LENGTH_SHORT).show()
                                activity?.onBackPressed()
                            } else
                                Toast.makeText(context,
                                    "Note has NOT been deleted. Try Again!",
                                    Toast.LENGTH_SHORT).show()
                        }
                    })
                setNegativeButton("No",
                    DialogInterface.OnClickListener { _, _ ->
                    })
            }
            builder.create()
        }
    }
}